/**
 * @file opt.c
 * @author AMOUSSOU Zinsou Kenneth (www.gitlab.com/azinke)
 * @brief CLI arguments/options parsing
 * @version 0.1
 * @date 2022-08-05
 * 
 * @copyright Copyright (c) 2022
 * 
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "opt.h"


/**
 * @brief Create the CLI parser
 *
 * @param name Name of the program
 * @param description Description of the program
 * @return parser_t 
 */
parser_t init_parser(const char* name, const char* description) {

  parser_t parser = {
    .name = (char*) name,
    .description = (char*) description,
    .first_arg = NULL,
  };
  return parser;
}

/**
 * @brief Add a CLI option to a parser
 * 
 * @param parser Pointer to the target parser
 * @param option Option to be added
 * @return parser_t 
 */
parser_t add_arg(parser_t parser, option_t *option) {

  if (option->args == NULL && option->argl == NULL) {
    printf("Error: No argument provided\n");
    return parser;
  }

  arg_t* argument = (arg_t*)malloc(sizeof(arg_t));

  if (argument == NULL) {
    printf("Error: Memory allocation failed\n");
    return parser;
  }

  argument->next = NULL;
  argument->opt = option;
  argument->is_set = 0;

  printf("\nAdding argument: %s | %s\n", argument->opt->args, argument->opt->argl);

  if (parser.first_arg != NULL) argument->next = parser.first_arg;
  parser.first_arg = argument;

  int count=0;
  for (arg_t* arg = parser.first_arg; arg != NULL && arg->opt!=NULL; arg = arg->next) {
    printf("arg: %s | %s\n", arg->opt->args, arg->opt->argl);
    count++;
  }
  printf("Total arguments: %d\n", count);
  return parser;
}

/**
 * @brief Free the resources allocated to a parser
 * 
 * @param parser 
 * @return void 
 */
void free_parser(parser_t *parser) {

  arg_t *arg = parser->first_arg;
  while (parser->first_arg != NULL) {
    for (arg = parser->first_arg; arg->next != NULL; arg = arg->next);
    free(arg->opt->value);
    free(arg);
  }
}


/**
 * @brief Print the help related to a parser
 * 
 * @param parser Pointer to the parser
 */
void print_help(parser_t* parser) {
  printf("usage: %s", parser->name);
  arg_t *argc = parser->first_arg;
  while(argc != NULL) {
    if (argc->opt->args != NULL) printf(" [%s]", argc->opt->args);
    else printf(" [%s]", argc->opt->argl);
    argc = (arg_t*)argc->next;
  }

  printf("\n\n%s\n\n", parser->description);
  printf("options:\n");

  argc = parser->first_arg;
  const int buffer_size = 32;
  char buf[buffer_size];
  while(argc != NULL) {
    memset(buf, '\0', buffer_size);
    if (argc->opt->args != NULL){
      strcpy(buf, argc->opt->args);
      strcat(buf, ", ");
    }
    if (argc->opt->argl != NULL) strcat(buf, argc->opt->argl);
    printf("    %-18s ", buf);
    printf("%s \n", argc->opt->help);
    argc = (arg_t*)argc->next;
  }
  printf("\n");

  printf("--- --- --- --- --- ---\n\n");
  printf("TOML config files\n\n");
  printf("TOML config files are used to define capture parameters for the TDA. ");
  printf("The TOML format uses key-value pairs\nto define the parameters; in a configuration ");
  printf("file, definition for profiles, chirps and frams for every\ndevice should be present.");
  printf("\n");
}


/**
 * @brief Parse the CLI arguments
 *
 * @param parser Parser
 * @param argc Argument counts
 * @param argv CLI arguments
 * @return int 
 */
int parse(parser_t *parser, int argc, char* argv[]) {

  int count=0;
  for (arg_t* arg = parser->first_arg; arg != NULL; arg = arg->next) count++;
  printf("\nTotal valid arguments: %d\n", count);

  if (argc <= 1) return 0; // No arguments provided
  for (int idx = 1; idx < argc; idx++) {

    arg_t* arg = parser->first_arg;
    printf("idx: %d, ", idx);
    printf("arg: %s\n", argv[idx]+2);
    while (arg!=NULL && !strcmp(argv[idx]+1, arg->opt->args) && !strcmp(argv[idx]+2, arg->opt->argl)) {
      printf("skip argument\n");
      arg=arg->next; // Get the argument from the list
    }

    printf("idx: %d, ", idx);
    printf("arg: %s | ", argv[idx]);

    switch (arg->opt->type) { // Check the type of the argument

      case OPT_BOOL: { // Boolean argument

        // Set the boolean as "True" when present
        arg->opt->value = (unsigned char*)malloc(sizeof(char));
        *(arg->opt->value) = 1;
        break;
      }

      case OPT_SHORT: { // Short integer argument

        arg->opt->value = (unsigned char*)malloc(sizeof(short));
        sscanf(argv[idx+1], "%hi", (short*)arg->opt->value);
        idx++; // skip the next CLI entry
        break;
      }

      case OPT_INT: { // Integer argument

        arg->opt->value = (unsigned char*)malloc(sizeof(int));
        sscanf(argv[idx+1], "%d", (int*)arg->opt->value);
        idx++; // skip the next CLI entry
        break;
      }

      case OPT_FLOAT: { // Float argument

        arg->opt->value = (unsigned char*)malloc(sizeof(float));
        sscanf(argv[idx+1], "%f", (float*)arg->opt->value);
        idx++; // skip the next CLI entry
        break;
      }

      case OPT_STR: { // String argument

        size_t size = strlen(argv[idx+1]);
        arg->opt->value = (unsigned char*)malloc(size);
        strncpy(arg->opt->value, argv[idx+1], size);
        idx++; // skip the next CLI entry
        break;
      }
    }

    if (arg->opt->callback != NULL) arg->opt->callback(); // Call the callback function if provided
    arg->is_set = 1; // Set the argument as set
  }
}


/**
 * @brief Read the value of the CLI argument
 * 
 * @param parser 
 * @param cli_arg 
 * @return void* 
 */
void* get_option(parser_t *parser, char *cli_arg) {
  arg_t *arg = parser->first_arg;
  while (arg != NULL) {
    if (is_arg(cli_arg, arg) == OPT_SUCCESS) {
      if (arg->is_set) return arg->opt->value;
      return arg->opt->default_value;
    }
    arg = (arg_t*)arg->next;
  }
  return NULL;
}


/**
 * @brief Check if a CLI argument provided match with a predefined option
 *
 * @param cli_arg Tag of the command line argument - Should start with a single
 *                or double dash character
 * @param arg Predefined argument
 * @return int Status.
 *    EOPT_NO_ARG       : Not a CLI argument
 *    EOPT_SUCESS       : Matched found
 *    EOPT_ARG_NO_MATCH : Not matched
 */
int is_arg(char *cli_arg, arg_t *arg) {

  // Number of single or double dash at the begining of a CLI
  // argument
  printf("\ncli_arg: %s\n", cli_arg);
  int ndash = 0;
  if (cli_arg[0] == '-') ndash++;
  if (cli_arg[1] == '-') ndash++;

  printf("ndash: %d\n", ndash);
  int arglen = strlen(cli_arg + ndash);
  int smatch = -1;
  int lmatch = -1;
  printf("arglen: %d\n", arglen);
  if (arg==NULL) printf("arg is NULL\n");
  printf("arg is not NULL\n");
  if (arg->opt == NULL) printf("arg->opt is NULL\n");
  printf("arg->opt is not NULL\n");
  if (arg->opt->args != NULL) {
    printf("arg->opt->args is not NULL\n");
    printf("cli_arg + ndash: %s\n", cli_arg + ndash);
    printf("arglen: %d\n", arglen);
    printf("arg->opt->args: %s\n", arg->opt->args);
    smatch = strncmp(cli_arg + ndash, arg->opt->args + 1, arglen);
  }
  printf("smatch: %d\n", smatch);
  if (arg->opt->argl != NULL) lmatch = strncmp(cli_arg + ndash, arg->opt->argl + 2, arglen);
  printf("lmatch: %d\n", lmatch);
  if ((smatch == 0) || (lmatch == 0)) return OPT_SUCCESS;
  return EOPT_ARG_NO_MATCH;
}
