# ConvertForeignCurrency
an Android Application that can convert Foreign Currency using this free API: [Fixer](https://fixer.io/documentation).

# Dev Notes
Download or clone the code to your local
use android studio to import the project
compile it and run it

#Testing notes
 * personally I am not a fan of extensive unit test, there are lot of reasons for that
    1. one it will always break since the code base is constantly changing as it should be
    2. it makes it harder for refactoring
    3. it only tests part of the application component (hence its unit testing) but that means it doesn't quantify the overall application
    4. most importantly its slows the development process and harder since you need to use mocks

 * on the other hand end to end UI testing is much more powerful since it mitigates all the above points and it ensures the application is bug free
    and you don't have to worry about the code changes as long as your UI/UX stays the same.

reference for testing philosophy
https://youtu.be/7Y3qIIEyP5c