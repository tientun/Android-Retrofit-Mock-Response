#Retrofit mock response [![CircleCI](https://circleci.com/gh/tientun/Android-Retrofit-Mock-Response/tree/master.svg?style=svg)](https://circleci.com/gh/tientun/Android-Retrofit-Mock-Response/tree/master) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.tientun/retrofit-mock-response/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.tientun/retrofit-mock-response)

##Features
* Create fake response from json files
* Support http methods

##Usage
#### 1. Include library
**Maven dependency:**
``` xml
<dependency>
	<groupId>com.github.tientun</groupId>
	<artifactId>retrofit-mock-response</artifactId>
	<version>1.0.0</version>
</dependency>
```

or

**Gradle dependency:**
``` groovy
compile 'com.github.tientun:retrofit-mock-response:1.0.0'
```

#### 2. Retrofit client configs
Create OkHttpClient and add FakeInterceptor(context)
```
final OkHttpClient client = new OkHttpClient
                       .Builder()
                       .addInterceptor(new FakeInterceptor(context))
                       .build();
```

Create Retrofit with baseUrl("http://mock.api")

```
final Retrofit retrofit = new Retrofit.Builder()
                       .addConverterFactory(GsonConverterFactory.create())
                       .baseUrl("http://mock.api")
                       .client(client)
                       .build();
```

#### 3. Create asset folder "mock.api" debug type

```
src/debug/assets/mock.api/
                        api1/
                        api2/
                        api3/
                            index.json
                            getUsers.json
                            user.json
                            ...
```

## License

    Copyright 2016 Tien Hoang

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.