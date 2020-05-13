package com.smatechnologies.msazure.vm.interfaces;

public interface IConstants {


	public interface Characters {
		public static final String AMPHERSAN = "&";
		public static final String ASTERIX = "*";
		public static final String BACK_SLASH = "\\";
		public static final String BRACE_CLOSE = "}";
		public static final String BRACE_OPEN = "{";
		public static final String BRACKET_CLOSE = "]";
		public static final String BRACKET_OPEN = "[";
		public static final String COLON = ":";
		public static final String COMMA = ",";
		public static final String DASH = "-";
		public static final String DOLLAR = "$";
		public static final String DOT = ".";
		public static final String EMPTY_STRING = "";
		public static final String EQUALS = "=";
		public static final String ESCAPED_DOT = "\\.";
		public static final String GREATER_THAN = ">";
		public static final String LESS_THAN = "<";
		public static final String MINUS = "-";
		public static final String NEWLINE = "\n";
		public static final String PARENTHESE_OPEN = "(";
		public static final String PARENTHESE_CLOSE = ")";
		public static final String PERCENT = "%";
		public static final String PERIOD = ".";
		public static final String PLUS = "+";
		public static final String QUOTE = "\"";
		public static final String SEMI_COLON = ";";
		public static final String SLASH = "/";
		public static final String SPACE = " ";
		public static final String SINGLE_QUOTE = "'";
		public static final String UNDERSCORE = "_";

	}	
	
	public interface General {
		public static final String SOFTWARE_VERSION = "1.0.0";
		public static final String SYSTEM_USER_DIRECTORY = "user.dir";
		public static final String TRUE = "True";
		public static final String TRUE_LOWER_CASE = "true";
		public static final String SYSTEM_PROPERTY_USER_NAME = "user.name";
		public static final String CONFIG_FILE_NAME = "Connector.config";
		public static final String DEBUG_ON = "ON";
	}

	public interface Configuration {
		public static final String CONNECTOR_HEADER = "CONNECTOR";
		public static final String CONNECTOR_NAME = "NAME";
		public static final String CONNECTOR_DEBUG = "DEBUG";
		public static final String MSAZURE_HEADER = "MSAZURE";
		public static final String MSAZURE_TENANT = "TENANT";
		public static final String MSAZURE_SUBSCRIPTION = "SUBSCRIPTION";
		public static final String MSAZURE_CLIENT = "CLIENT";
		public static final String MSAZURE_KEY = "KEY";
		public static final String MSAZURE_POLL_INTERVAL = "POLL_INTERVAL";
		public static final String MSAZURE_INITIAL_POLL_DELAY = "POLL_DELAY";
		public static final String OPCONAPI_HEADER = "OPCON API";
		public static final String OPCONAPI_ADDRESS = "OPCONAPI_ADDRESS";
		public static final String OPCONAPI_USING_TLS = "OPCONAPI_USING_TLS";
		public static final String OPCONAPI_TOKEN = "OPCONAPI_TOKEN";
	}

	interface OpConApiResults {
		public static final String RESULT_SUCCESS = "Success"; 
	}

	interface LogBackConstants {

		public static final String LOG_PATH = "logback.path";
		public static final String DEBUG_DEPENDENCIES = "logback.debug.dependencies";
		public static final String DEBUG_API = "logback.debug.api";
		public static final String LEVEL_STDOUT_KEY = "logback.level.stdout";
		public static final String LEVEL_FILE_KEY = "logback.level.file";
		public static final String STDOUT_PATTERN_KEY = "logback.stdout.pattern";
		public static final String MAXHISTORY_FILE_KEY = "logback.maxhistory.file";
		public static final String LEVEL_DEBUG_VALUE = "TRACE";
		public static final String STDOUT_PATTERN_DEBUG_VALUE = "FULL";
    }

}
