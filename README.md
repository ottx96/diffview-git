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
Usage: diffview [-hvV] [--debug] [-d[=<inputDirectory>]] [-o
                [=<outputDirectory>]] <files>...
      <files>...   The file whose history/diffviews to generate.
  -d, --directory-in[=<inputDirectory>]
                   Sets the directory root to read from.
                   Has to be inside of a valid git repository.
                     Default:
      --debug      Sets the output to debug.
  -h, --help       Show this help message and exit.
  -o, --directory-out[=<outputDirectory>]
                   Sets the directory to output .html files to.
                   Files wll be created as [file name].html
                   e.g.: README.md.html
                     Default: diffview-generated/
  -v, --verbose    Sets the output to verbose.
  -V, --version    Print version information and exit.
```

### Convert to html using java
Run the following command inside of a git repository.  
`java -jar diffview-git.jar [file..]`

### Convert to html using Docker

#### Build the Container
`docker build -t "diffview:latest" .`  
or  
`gradlew buildDockerImage`

#### Run the Container
`docker run --rm -v "[path/to/repo]:/git" diffview [..arguments]`

Example:    
`docker run --rm -v "$(pwd):/git" diffview README.md src/main/resources/logback.xml`

## Roadmap
- [ ] Add Paramter to limit the count of commits to display
- [ ] Add Paramter to enlargen the displayed length of the file
- [ ] Directly Serve the HTTP via Web Server (netty?) after converting the history
- [ ] Add Parameter to auto-stop the Web Server (javascript / time limit)
