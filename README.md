## A simple `git diff` to html converter

This Project is meant to be run as CLI Application.  
I'll provide a `Dockerfile` to build your own images using `GraalVM native images`.

Also, I'll provide another shellscript, which creates a container with a running webserver.  
So you can directly see the formatted diff locally.

[![Gradle Build](https://github.com/ottx96/diffview-git/actions/workflows/shadow-jar.yml/badge.svg)](https://github.com/ottx96/diffview-git/actions/workflows/shadow-jar.yml)
[![Native Image](https://github.com/ottx96/diffview-git/actions/workflows/native-image.yml/badge.svg)](https://github.com/ottx96/diffview-git/actions/workflows/native-image.yml)
[![Dependabot Status](https://api.dependabot.com/badges/status?host=github&repo=ottx96/diffview-git)](https://dependabot.com)

## Usage
```text
Usage: diffview [-hvV] [--debug] [-d[=<inputDirectory>]]
                [--no-original-extension[=<omitOriginalExtensions>]] [-o
                [=<outputDirectory>]] <files>...
      <files>...   The file whose history/diffviews to generate.
  -d, --directory-in[=<inputDirectory>]
                   Sets the directory root to read from.
                   Has to be inside of a valid git repository.
                     Default:
      --debug      Sets the output to debug.
  -h, --help       Show this help message and exit.
      --no-original-extension[=<omitOriginalExtensions>]
                   Omits the original extension for output files.
                   e.g.: README.md --> README.html instead of README.md.html
                   or build.gradle --> build.html
                     Default: false
  -o, --directory-out[=<outputDirectory>]
                   Sets the directory to output .html files to.
                   Files wll be created as [file name].html
                   e.g.: README.md.html
                     Default: diffview-generated/
  -v, --verbose    Sets the output to verbose.
  -V, --version    Print version information and exit.
```

## Using the Program
Run the following commands inside of a git repository.  

#### Using Native Image (preferred)
`diffview [-hvV] [--debug] [-d[=<inputDirectory>]]
                [--no-original-extension[=<omitOriginalExtensions>]] [-o
                [=<outputDirectory>]] <files>...`  

Example:  
`diffview -v README.md`

#### Using Docker
`docker run --rm -v "[path/to/repo]:/git" diffview [-hvV] [--debug] [-d[=<inputDirectory>]]
                [--no-original-extension[=<omitOriginalExtensions>]] [-o
                [=<outputDirectory>]] <files>...`

Example:    
`docker run --rm -v "$(pwd):/git" diffview -v README.md`

#### Using Java
`java -jar diffview-git.jar [-hvV] [--debug] [-d[=<inputDirectory>]]
                [--no-original-extension[=<omitOriginalExtensions>]] [-o
                [=<outputDirectory>]] <files>...`  
                
Example:   
`java -jar doffview-git.jar -v README.md`

## Building from Source
If you want to create the Binaries from Source, here you go!  

#### Build using Java
run `gradlew assemble` inside of the repository.  
This will generate the Libraries (.jar) insiddde of the `build/` folder.

#### Build as Native Image (GraalVM)
after compiling the libraries (.jar), you can use GraalVM's native-image tool to generate a native image.  
run `native-image -cp "application.jar:libs/*.jar:resources/*" com.github.ottx96.Entrypoint` inside of the folder `build/layers`.

#### Build using Docker
run `docker build -t "diffview:latest" .` or `gradlew buildDockerImage` inside of the repository.

## Roadmap
- [ ] Add Paramter to limit the count of commits to display
- [ ] Add Paramter to enlargen the displayed length of the file
- [ ] Directly Serve the HTTP via Web Server (netty?) after converting the history
- [ ] Add Parameter to auto-stop the Web Server (javascript / time limit)
