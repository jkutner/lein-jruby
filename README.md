# lein-jruby

This is a [Leiningen](https://github.com/technomancy/leiningen "Leiningen") plugin that allows you to run JRuby commands from a Leiningen project.

## Configuration

Add `[lein-jruby "0.1.0"]` to the `:plugins` section of your `project.clj`

## Usage

This plugin provides the following commands:

    $ lein jruby bundle
    $ lein jruby irb
    $ lein jruby rake
    $ lein jruby -S XXXX
    $ lein jruby -e XXXX
    $ lein jruby -v

For example, you can install some gem with bundler:

    $ lein jruby bundle install --without production

Or you run a JRuby script like this:

    $ lein jruby -S my_script.rb

Or you can execute a Rake task:

    $ lein jruby rake assets:precompile

Or you can execute an inline script:

    $ lein jruby -e "puts 42"

And of course you can start an irb session:

    $ lein jruby irb
    jruby-1.6.7 :001 >

You can also see what version of JRuby is running:

    $ lein jruby -v
    jruby 1.6.7 (ruby-1.8.7-p357) (2012-02-22 3e82bc8) (Java HotSpot(TM) 64-Bit Server VM 1.6.0_29) [darwin-x86_64-java]

Eventually, an excplicit dependencies on a particular version of ruby in your project.clj will override this.  But for now it's pegged to 1.6.7

## Configuration

_Since 0.1.1-SNAPSHOT_

You can configure the plugin by adding a `:jruby-options` to your `project.clj`.  The supported options are:

+  `:mode` (either "1.8" or "1.9")

+  `:bundler-version`

See the `sample/project.clj` for examples.

## Using RubyGems

_Since 0.1.1-SNAPSHOT_

The plugin is intened to work without a local installation of JRuby.  It uses the JRuby provided by the `org.jruby/jruby-complete`.  Thus, there won't be a natural RubyGems installation directory.  Instead, `lein-jruby` will install all your Gems to the `.lein-gems` directory under your project's root.

When you run:

    lein jruby gem install json

You'll see this:

    project-root/
    `-- .lein-gems/
        `-- gems/
            |-- bin/
            |-- cache/
            |-- doc/
            |-- gems/
                `-- json-1.6.6-java
            `-- specifications/
    

And if you install via Bundler it will be a little different (there will be a `jruby` dir instead of a `rubygems` dir).  But it will all be under `.lein-gems`.  Eventually this will be configurable. 

## Where do I put my Ruby code?

_Since 0.1.1-SNAPSHOT_

Right now, you'll need to put your Ruby code under the `src` directory in your project root.  When you run `lein jruby irb` or any other command, those sources should be on your loadpath.

Take a look at the example in the `sample` directory.  There is a `src/config.ru` in it.  When you run `rackup` like this:

    lein jruby -S rackup
    [2012-05-09 17:18:04] INFO  WEBrick 1.3.1
    [2012-05-09 17:18:04] INFO  ruby 1.8.7 (2012-02-22) [java]
    [2012-05-09 17:18:04] INFO  WEBrick::HTTPServer#start: pid=6367 port=9292

It will boot up the app in that app.  The app will have all your leiningen dependencies, and your clojure code on the classpath too!

## TODO

+  add hooks

+  add configuration: bundler version, src dir

+  allow jruby in project.clj to override plugin's default version

+  irb isn't picking up the GEM_HOME (because of the way the task works)

+  make it bundler smart. if there is a Gemfile, you don't need to add `bundle exec` (but we'll also need to add a way to turn that off -- kind of like warbler does)

