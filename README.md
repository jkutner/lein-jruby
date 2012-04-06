# lein-jruby

This is a [Leiningen](https://github.com/technomancy/leiningen "Leiningen") plugin that allows you to run JRuby commands from a Leiningen project.

## Configuration

Clone this project with Git, then run:

    $ lein install

Then add `[lein-jruby "0.1.0-SNAPSHOT"]` to the `:plugins` section of your `project.clj`

## Usage

This plugin provides the following commands:

    $ lein jruby bundle
    $ lein jruby irb
    $ lein jruby rake
    $ lein jruby exec
    $ lein jruby -S XXXX
    $ lein jruby -e XXXX

For example, you can install some gem with bundler:

    $ lein jruby bundle install --without production

Or you run a JRuby script like this:

    $ lein jruby exec my_script.rb

Or you can execute a Rake task:

    $ lein jruby rake assets:precompile

Or you can execute an inline script:

    $ lein jruby -e puts(42)

And of course you can start an irb session:

    $ lein jruby irb
    jruby-1.6.7 :001 >


## Problems

When a command fails (for example if you try to execute a Rake task that doesnt exist) you get a really nasty message:

    Permissions.java:196:in `checkExit': org.apache.tools.ant.ExitException: Permission (java.lang.RuntimePermission exitVM) was not granted.
        from Runtime.java:88:in `exit'
        from System.java:921:in `exit'
        from Main.java:190:in `main'
        from NativeMethodAccessorImpl.java:-2:in `invoke0'
        from NativeMethodAccessorImpl.java:39:in `invoke'
        from DelegatingMethodAccessorImpl.java:25:in `invoke'
        from Method.java:597:in `invoke'
        from ExecuteJava.java:217:in `run'
        from ExecuteJava.java:152:in `execute'
        from Java.java:764:in `run'
        from Java.java:218:in `executeJava'
        from Java.java:132:in `executeJava'
        from Java.java:105:in `execute'
        from NativeMethodAccessorImpl.java:-2:in `invoke0'
        from NativeMethodAccessorImpl.java:39:in `invoke'
        from DelegatingMethodAccessorImpl.java:25:in `invoke'
        from Method.java:597:in `invoke'
        from Reflector.java:90:in `invokeMatchingMethod'
        from Reflector.java:265:in `invokeNoArgInstanceMember'
        from jruby.clj:19:in `doInvoke'

This is just an annoyance though - no affect on behavior.  It'll get fixed soon.

## TODO

+  add hooks
