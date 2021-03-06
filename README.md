
About [![Build Status](https://travis-ci.org/d0x/nchadoop.png?branch=master)](https://travis-ci.org/d0x/nchadoop)
======

nchadoop is a *ncdu* like file browser for Hadoop to identify wasted disc space.

It scans folders on hdfs and procudes a cli like this:

```
 --- /user/christian/thrift/
                        /..
   20.9MiB [##########]  libtestgencpp.a
    6.5MiB [###       ]  DebugProtoTest_types.o
    4.6MiB [##        ]  ThriftTest_types.o
    3.9MiB [#         ]  Benchmark
    1.3MiB [          ]  ThriftTest_extras.o
    1.2MiB [          ]  DebugProtoTest_extras.o
  728.0KiB [          ]  OptionalRequiredTest_types.o
  560.0KiB [          ]  libprocessortest.a
  252.0KiB [          ]  ChildService.o
  216.0KiB [          ]  ParentService.o
   96.0KiB [          ]  proc_types.o
```

Latest supported version CDH version: 2.6.0-cdh5.10.1


License
=======

    Copyright 2018 Yieldlab AG

    Copyright 2013 Christian Schneider

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
