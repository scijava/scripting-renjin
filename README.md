[![](http://jenkins.imagej.net/job/scripting-R/lastBuild/badge/icon)](http://jenkins.imagej.net/job/scripting-R/)

# R Scripting

__NB: This library is an experimental work in progress. Not yet functional!__

This library provides a
[JSR-223-compliant](https://en.wikipedia.org/wiki/Scripting_for_the_Java_Platform)
scripting plugin for the [R](http://www.r-project.org/) language.

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
```
# @String label
# @OUTPUT String version
version = paste("[", label, "] ", R.version.string, sep = "")
```

Outstanding issues:

* Fix bug where `DisplayPostprocessor` thinks output is null
* Support multiple output values
* Compare this Rserve-based solution to one using rJava
* Eliminate FIXME blocks
  - Reduce redundancy of "bindings" code across scripting languages
  - Implement get/set variable methods properly for R
* Add support for Dataset, DatasetView, ImageDisplay
