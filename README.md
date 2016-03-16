# cmanager

## License

cmanager is distributed under the [The GNU General Public License v3](http://www.gnu.org/licenses/gpl-3.0-standalone.html).

## Build

### Prerequisites

- Java development kit (JDK) in version 7
- You need to provide API keys for compiling cmanager. See next section for details.

### API keys

Request your personal API keys for the supported [OpenCaching](http://www.opencaching.eu/) sites, currently:
* [opencaching.de OKAPI signup](https://www.opencaching.de/okapi/signup.html)

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
