/* Copyright © 2012 Andreas Norberg <anorber@kth.se>
 *
 * Permission is granted, USE AT OWN RISK
 * See http://sam.zoy.org/wtfpl
 */
package com.github.anorber.optget;

import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.List;

/**
 * Parser for command line options.
 *
 * This class helps programs to parse the command line arguments. It
 * supports the same conventions as the Unix getopt() function
 * (including the special meanings of arguments of the form `-'
 * and `--'). Long options similar to those supported by GNU software
 * may be used as well via an optional third argument.
 *
 * @author Andreas Norberg
 */
public class Getopt {

	private final List<Option> opts;
	private final List<String> args;

	/**
	 * Parses command line options and parameter list. <code>args</code>
	 * is the argument list to be parsed. <code>shortopts</code> is the
	 * string of option letters that the script wants to recognize, with
	 * options that require an argument followed by a colon (i.e., the
	 * same format that Unix getopt() uses).
	 *
	 * @param args       the arguments to parse
	 * @param shortopts  the option letters to recognize
	 * @throws IllegalStateException
	 */
	public Getopt(final String[] args, final String shortopts) throws IllegalStateException {
		this(args, shortopts, new String[0]);
	}

	/**
	 * Parses command line options and parameter list. <code>args</code>
	 * is the argument list to be parsed. <code>shortopts</code> is the
	 * string of option letters that the script wants to recognize, with
	 * options that require an argument followed by a colon (i.e., the
	 * same format that Unix getopt() uses). <code>longopts</code> is a
	 * list of strings with the names of the long options which should
	 * be supported. The leading '--' characters should not be included
	 * in the option name. Options which require an argument should be
	 * followed by an equal sign ('=').
	 *
	 * @param args       the arguments to parse
	 * @param shortopts  the option letters to recognize
	 * @param longopts   the names of long options
	 * @throws IllegalStateException
	 */
	public Getopt(final String[] args, final String shortopts, final String... longopts) throws IllegalStateException {
		if (args == null)
			throw new NullPointerException();
		if (shortopts == null)
			throw new NullPointerException();
		if (longopts == null)
			throw new NullPointerException();

		this.args = new ArrayList<String>(asList(args));  // apparently asList can't be modified
		this.opts = new ArrayList<Option>();

		getopt(shortopts, longopts);
	}


	/**
	 * Returns the list of program arguments left after the option list
	 * was stripped.
	 *
	 * @return  the rest of the args
	 */
	public String[] getArgs() {
		return args.toArray(new String[0]);
	}

	/**
	 * Gets the option at the specified index
	 *
	 * @param index  the index of the option to return
	 * @return       the name of the option
	 */
	public String getOptionAt(final int index) {
		return opts.get(index).getOption();
	}

	/**
	 * Gets the arguments to the option at the specified index
	 *
	 * If no arguments was specified, the empty string is returned;
	 *
	 * @param index  the index of the argument to return
	 * @return       the argument given to the specified option
	 */
	public String getValueAt(final int index) {
		return opts.get(index).getArgument();
	}

	/**
	 * Returns the number of options in this list.
	 *
	 * @return  the number of opts
	 */
	public int getOptionsCount() {
		return opts.size();
	}

	/**
	 * Returns the arguments for the given option as an array
	 *
	 * @param option  the option name
	 * @return        the arguments
	 */
	public String[] getArguments(String option) {
		List<String> result = getArgumentsList(option);
		return result.toArray(new String[0]);
	}

	/**
	 * Returns the arguments for the given option as a list
	 *
	 * @param option  the option name
	 * @return        the arguments
	 */
	public List<String> getArgumentsList(String option) {
		List<String> result = new ArrayList<String>();
		for (Option o : opts)
			if (o.getOption().equals(option))
				result.add(o.getArgument());
		return result;
	}

	/**
	 * Tells if an option has been specified
	 *
	 * @param option  the option name
	 * @return        if an option with the given name was found
	 */
	public boolean hasOptions(String option) {
		return !getArgumentsList(option).isEmpty();
	}

	/**
	 * Returns the number of times this option was found
	 *
	 * @param option  the option name
	 * @return        the number of times the option was specified
	 */
	public int getOptionCount(String option) {
		return getArgumentsList(option).size();
	}

	/**
	 * Returns the argument of an option if it was found once.
	 *
	 * @param option  the option name
	 * @return        argument as a string
	 */
	public String getArgumentString(String option) {
		List<String> result = getArgumentsList(option);
		if (result.size() == 1)
			return result.get(0);
		return null;
	}


	private void addOption(final String option, final String value) {
		opts.add(new Option(option, value));
	}

	private void getopt(final String shortopts, final String[] longopts) throws IllegalStateException {
		while (!args.isEmpty()) {
			final String opt = args.get(0);
			if (!opt.startsWith("-"))
				break;
			if (opt.equals("-"))
				break;
			args.remove(0);
			if (opt.equals("--"))
				break;
			if (opt.startsWith("--"))
				long_opts(opt.substring(2), longopts);
			else
				short_opts(opt.substring(1), shortopts);
		}
	}

	private void short_opts(final String optstring, final String shortopts) throws IllegalStateException {
		for (int i = 0; i < optstring.length(); ++i) {
			final char opt = optstring.charAt(i);
			final String option = "-" + opt;
			if (short_has_arg(opt, shortopts)) {
				if (i + 1 == optstring.length()) {
					if (args.isEmpty())
						throw new IllegalStateException("option " + option + " requires argument: " + opt);
					addOption(option, args.remove(0));
				} else {
					addOption(option, optstring.substring(i + 1));
				}
				break;
			} else {
				addOption(option, "");
			}
		}
	}

	private void long_opts(final String optstr, final String[] longopts) throws IllegalStateException {
		final int i = optstr.indexOf("=");
		if (i > 0)
			long_optsargs(optstr.substring(0, i), optstr.substring(i + 1), longopts);
		else
			long_optsargs(optstr, null, longopts);
	}

	private void long_optsargs(final String opt, final String optarg, final String[] longopts) throws IllegalStateException {
		final List<String> possibilities = getPossibilities(opt, longopts);
		if (possibilities.contains(opt)) {
			long_optargs_1(optarg, false, opt);
		} else if (possibilities.contains(opt + "=")) {
			long_optargs_1(optarg, true, opt);
		} else {
			if (possibilities.size() != 1)
				throw new IllegalStateException("option --" + opt + " not a unique prefix");
			final String uniMatch = possibilities.get(0);
			if (uniMatch.endsWith("="))
				long_optargs_1(optarg, true, uniMatch.substring(0, uniMatch.length() - 1));
			else
				long_optargs_1(optarg, false, uniMatch);
		}
	}

	private void long_optargs_1(final String optarg, final boolean has_arg, final String unique_match) throws IllegalStateException {
		final String option = "--" + unique_match;

		if (has_arg) {
			if (optarg == null) {
				if (args.isEmpty())
					throw new IllegalStateException("option " + option + " requires argument: " + unique_match);
				addOption(option, args.remove(0));
			} else {
				addOption(option, optarg);
			}
		} else if (optarg != null) {
			throw new IllegalStateException("option " + option + " must not have an argument: " + unique_match);
		} else {
			addOption(option, "");
		}
	}

	private List<String> getPossibilities(final String opt, final String[] longopts) throws IllegalStateException {
		final List<String> possibilities = new ArrayList<String>();
		for (final String o : asList(longopts))
			if (o.startsWith(opt))
				possibilities.add(o);
		if (possibilities.isEmpty())
			throw new IllegalStateException("option --" + opt + " not recognized");
		return possibilities;
	}

	private boolean short_has_arg(final char opt, final String shortopts) throws IllegalStateException {
		if (opt != ':')
			for (int i = 0; i < shortopts.length(); ++i)
				if (shortopts.charAt(i) == opt)
					return shortopts.startsWith(":", i + 1);
		throw new IllegalStateException("option -" + opt + " not recognized");
	}


	private final class Option {

		private final String option;
		private final String value;

		private Option(String option, String value) {
			this.option = option;
			this.value = value;
		}

		public String getOption() {
			return option;
		}

		public String getArgument() {
			return value;
		}
	}
}
