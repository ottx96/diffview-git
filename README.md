## A simple `git diff` to html converter

This Project is meant to be run as CLI Application.  
I'll provide a `Dockerfile` to build your own images using [GraalVM native images](https://www.graalvm.org/).

Also, I'll provide another shellscript, which creates a container with a running webserver.  
This way, you can directly see the formatted diff locally.

[![Gradle Build](https://github.com/ottx96/diffview-git/actions/workflows/shadow-jar.yml/badge.svg)](https://github.com/ottx96/diffview-git/actions/workflows/shadow-jar.yml)
[![Native Image](https://github.com/ottx96/diffview-git/actions/workflows/native-image.yml/badge.svg)](https://github.com/ottx96/diffview-git/actions/workflows/native-image.yml)
[![Dependabot Status](https://api.dependabot.com/badges/status?host=github&repo=ottx96/diffview-git)](https://dependabot.com)

## Usage
```text
Usage: diffview [-hvV] [--debug] [-a[=<action>]] [--no-original-extension
                [=<omitOriginalExtensions>]] [-o[=<outputDir>]] [-R
                [=<repository>]] <files>...
      <files>...            The file(s) whose history/diffviews to generate.
  -a, --action[=<action>]   Which action to execute.
                            Possible values: (LOG|DIFF)
                              Default: LOG
      --debug               Sets the output to debug.
  -h, --help                Show this help message and exit.
      --no-original-extension[=<omitOriginalExtensions>]
                            Omits the original extension for output files.
                            e.g.: README.md --> README.html instead of README.
                              md.html
                            or build.gradle --> build.html
                              Default: false
  -o, --directory-out[=<outputDir>]
                            Sets the directory to output .html files to.
                            Files wll be created as [file name].html
                            e.g.: README.md.html
                              Default: diffview-generated/
  -R, --repository, --directory-in[=<repository>]
                            Sets the directory root to read from.
                            Has to be inside of a valid git repository.
                              Default:
  -v, --verbose             Sets the output to verbose.
  -V, --version             Print version information and exit.
```

## Using the Program
Run the following commands inside of a git repository.  

#### Using Native Image (preferred)
`diffview [-hvV] [--debug] [-a[=<action>]] [--no-original-extension
        [=<omitOriginalExtensions>]] [-o[=<outputDir>]] [-R
        [=<repository>]] <files>...`  

Example:  
`diffview -v README.md`

#### Using Docker
`docker run --rm -v "[path/to/repo]:/git" diffview [-hvV] [--debug] [-a[=<action>]] [--no-original-extension
        [=<omitOriginalExtensions>]] [-o[=<outputDir>]] [-R
        [=<repository>]] <files>...`

Example:    
`docker run --rm -v "$(pwd):/git" diffview -v README.md`

#### Using Java
`java -jar diffview-git.jar [-hvV] [--debug] [-a[=<action>]] [--no-original-extension
        [=<omitOriginalExtensions>]] [-o[=<outputDir>]] [-R
        [=<repository>]] <files>...`  
                
Example:   
`java -jar diffview-git.jar -v README.md`

## Preview
![image](https://user-images.githubusercontent.com/49874532/112373883-bceb4e80-8ce1-11eb-946f-f65cc3075a85.png)

## Building from Source
If you want to create the binaries from source, here you go!  

#### Build using Java
Run `gradlew assemble` inside of the repository.  
This will generate the libraries (.jar) inside of the `build/` folder.

#### Build as Native Image (GraalVM)
After compiling the libraries (.jar), you can use [GraalVM's native-image tool](https://www.graalvm.org/reference-manual/native-image/) to generate a native image.  
Run `native-image -cp "application.jar:libs/*.jar:resources/*" com.github.ottx96.Entrypoint` inside of the folder `build/layers`.

#### Build using Docker
Run `docker build -t "diffview:latest" .` or `gradlew buildDockerImage` inside of the repository.

## Roadmap
- [ ] Add parameter to limit the count of commits to display
- [ ] Add parameter to enlargen the displayed length of the file
- [ ] Directly serve the HTTP via web server (netty?) after converting the history
- [ ] Add parameter to auto-stop the web server (javascript / time limit)
