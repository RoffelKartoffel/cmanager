# cmanager

## License & Source Code

cmanager is distributed under the [The GNU General Public License v3](http://www.gnu.org/licenses/gpl-3.0-standalone.html).
The sources are available on GitHub ([link](https://github.com/RoffelKartoffel/cmanager)).

## Distribution / "Download"
- Releases are published on GitHubs as ["Releases"](https://github.com/RoffelKartoffel/cmanager/releases)
- If you are running ArchLinux as your OS you can refer to an [AUR version](https://aur.archlinux.org/packages/cmanager/) which is maintained by SammysHP




## Building from Source

### Prerequisites

- Java development kit (JDK) in version 7
- You need to provide API keys for compiling cmanager. See next section for details.

### API keys

Request your personal API keys for the supported [OpenCaching](https://www.opencaching.eu/) sites, currently:
* [opencaching.de OKAPI signup](https://www.opencaching.de/okapi/signup.html)
* [opencaching.pl OKAPI signup](https://www.opencaching.pl/okapi/signup.html)
* [opencaching.ro OKAPI signup](https://www.opencaching.ro/okapi/signup.html)
* [opencaching.nl OKAPI signup](https://www.opencaching.nl/okapi/signup.html)
* [opencaching.org.uk OKAPI signup](https://www.opencaching.org.uk/okapi/signup.html)
* [opencaching.us OKAPI signup](https://www.opencaching.us/okapi/signup.html)


Copy [`templates/oc_okapi.properties`](https://github.com/RoffelKartoffel/cmanager/blob/master/templates/oc_okapi.properties) to the root directory of the git repository.
Then edit `oc_okapi.properties` and insert your keys.

### Building with Gradle

Run `gradlew build` from the root directory of the git repository.

### Eclipse

With the release of Eclipse Mars it is possible to directly import Gradle projects.
Go to `File -> Import -> Gradle -> Gradle Project`, click `Next`, set the `Project root directory` by navigating to the root directory of the git repository
and finally click `Finish`.

If you have not run `gradlew build` yet, run the `build` task from the `Gradle Tasks` view and refresh the project afterwards, to create the generated source code files.

### JAR

To create a JAR file, run `gradlew jar`. The JAR file will be located in `build/libs`.

## Usage

### Starting the application with Gradle

Run `gradlew run` from the root directory of the git repository.
