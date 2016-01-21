[![](http://jenkins.imagej.net/job/scripting-renjin/lastBuild/badge/icon)](http://jenkins.imagej.net/job/scripting-renjin/)

# Renjin Scripting

This library provides a scripting plugin wrapping the [Renjin](http://www.renjin.org/)
Java implementation of the [R](http://www.r-project.org/) language.

It is implemented as a `ScriptLanguage` plugin for the [SciJava
Common](https://github.com/scijava/scijava-common) platform, which means that
in addition to being usable directly as a `javax.script.ScriptEngineFactory`,
it also provides some functionality on top, such as the ability to generate
lines of script code based on SciJava events.

For a complete list of scripting languages available as part of the SciJava
platform, see the
[Scripting](https://github.com/scijava/scijava-common/wiki/Scripting) page on
the SciJava Common wiki.

-----

Here is an example annotated R script:
```R
# @ScriptService ss
# @OUTPUT String name
language <- ss$getLanguageByName('R')
name <- language$languageName
```

Known limitations or quirks:

* Variables outside the Global environment scope may not persist after the script runs. If you need to access a variable after running the script, it is safer to use global assignment ("<<-" or "assign()");
* "Bean" accessors (obj$getName(), reutrns local variable "name") may automatically be stripped out, requiring access via "obj$name".
* Methods of Java objects must be referrenced via the dollar functions (obj$function)
