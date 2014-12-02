ixiaoshuo-android
=================

ixiaoshuo is a Chinese e-book reader, served online book reading. I'm developing this project with my work mates in past company, but it has closed before finish. This one was a reproduce practice of me.

'Cause this application as a online service, it of course needed a server to serving datas. But it's too expensive to deploy one, thus I benefit from GitHub Pages, making all remote datas hosted on [ixiaoshuo-data-center](https://github.com/vince-styling/ixiaoshuo-data-center). Eventually made the Application fully alive just like behind has a dynamic server does.

# Development

If you want to compile this project, you should make the `libs/android-support-v4.jar` as a library or install it into the local Maven repository :

```bash
mvn install:install-file
    -Dfile=libs/android-support-v4.jar
    -DgroupId=com.google.android
    -DartifactId=support-v4
    -Dversion=4.4.2_r1
    -DgeneratePom=true
    -Dpackaging=jar
```

You can download the compiled APK which at the project's root directory to experiencing it, following is what you're getting for.

![Runtime Screenshot](screenshot.png "screenshot")

![Runtime Screenshot](screenshot_finder.png "screenshot_finder")

License
=======

```
Copyright 2014 Vince Styling

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```



