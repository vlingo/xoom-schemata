// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

lexer grammar SchemaVersionDefinitionLexer;

///////////////////////////////
// schema types
///////////////////////////////

COMMAND   :      'command';
DATA      :      'data';
DOCUMENT  :      'document';
ENVELOPE  :      'envelope';
EVENT     :      'event';
METADATA  :      'metadata';

///////////////////////////////
// attribute types
///////////////////////////////

BOOLEAN   :        'boolean';
BYTE      :        'byte';
CHAR      :        'char';
DOUBLE    :        'double';
FLOAT     :        'float';
INT       :        'int';
LONG      :        'long';
SHORT     :        'short';
STRING    :        'string';

TIMESTAMP :        'timestamp';
TYPE      :        'type';
VERSION   :        'version';


///////////////////////////////
// operators and separators
///////////////////////////////

ASSIGN    :        '=';
ARRAY     :        '[]';
COMMA     :        ',';
DOT       :        '.';
MINUS     :        '-';
LBRACE    :        '{';
RBRACE    :        '}';
SEMI      :        ';';
COLON     :        ':';

///////////////////////////////
// whitespace and comments
///////////////////////////////

WS
  : [ \t\r\n\u000C]+ -> channel(HIDDEN)
  ;

COMMENT
  : '/*' .*? '*/'    -> channel(HIDDEN)
  ;

LINE_COMMENT
  :   '//' ~[\r\n]*  -> channel(HIDDEN)
  ;

///////////////////////////////
// literals
///////////////////////////////

BYTE_LITERAL
  : '0'
  | [1-9]
  | [1-9] [1-9]
  | '1' [1-2] [1-9]
  ;

DECIMAL_LITERAL
  : ('0' | [1-9] (Digits? | '_'+ Digits)) [lL]?
  ;

HEX_LITERAL
  : '0' [xX] [0-9a-fA-F] ([0-9a-fA-F_]* [0-9a-fA-F])? [lL]?
  ;

OCT_LITERAL
  : '0' '_'* [0-7] ([0-7_]* [0-7])? [lL]?
  ;

BINARY_LITERAL
  : '0' [bB] [01] ([01_]* [01])? [lL]?
  ;

FLOAT_LITERAL
  : (Digits '.' Digits? | '.' Digits) ExponentPart? [fFdD]?
  | Digits (ExponentPart [fFdD]? | [fFdD])
  ;

HEX_FLOAT_LITERAL
  : '0' [xX] (HexDigits '.'? | HexDigits? '.' HexDigits) [pP] [+-]? Digits [fFdD]?
  ;

BOOLEAN_LITERAL
  : 'true'
  | 'false'
  ;

CHAR_LITERAL
  : '\'' (~['\\\r\n] | EscapeSequence) '\''
  ;

STRING_LITERAL
  : '"' (~["\\\r\n] | EscapeSequence)* '"'
  ;

NULL_LITERAL
  : 'null'
  ;

///////////////////////////////
// identifiers
///////////////////////////////


TYPE_IDENTIFIER
  : CapitalLetter LetterOrDigit*
  ;

SEMANTIC_VERSION
  : Digits DOT Digits DOT Digits
  ;

IDENTIFIER
  : Letter LetterOrDigit*
  ;

ATTRIBUTE_TYPE_IDENTIFIER
  : TYPE_IDENTIFIER (COLON SEMANTIC_VERSION)?
  | ORG_IDENTIFIER COLON UNIT_IDENTIFIER COLON CONTEXT_IDENTIFIER COLON SCHEMA_IDENTIFIER (COLON SEMANTIC_VERSION)?
  ;

ORG_IDENTIFIER
  : IDENTIFIER
  ;

UNIT_IDENTIFIER
  : IDENTIFIER
  ;

CONTEXT_IDENTIFIER
  : IDENTIFIER (DOT IDENTIFIER)*
  ;

SCHEMA_IDENTIFIER
  : IDENTIFIER
  ;

///////////////////////////////
// fragment rules
///////////////////////////////

fragment ExponentPart
    : [eE] [+-]? Digits
    ;

fragment EscapeSequence
    : '\\' [btnfr"'\\]
    | '\\' ([0-3]? [0-7])? [0-7]
    | '\\' 'u'+ HexDigit HexDigit HexDigit HexDigit
    ;
fragment HexDigits
    : HexDigit ((HexDigit | '_')* HexDigit)?
    ;
fragment HexDigit
    : [0-9a-fA-F]
    ;
fragment Digits
    : [0-9] ([0-9_]* [0-9])?
    ;
fragment LetterOrDigit
    : Letter
    | [0-9]
    ;
fragment CapitalLetter
    : [A-Z]
    ;
fragment Letter
    : [a-zA-Z_]
    ;
