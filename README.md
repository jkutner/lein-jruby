# lein-jruby

This is a [Leiningen](https://github.com/technomancy/leiningen "Leiningen") plugin that allows you to run JRuby commands from a Leiningen project.

## Configuration

Add `[lein-jruby "0.1.0"]` to the `:plugins` section of your `project.clj`

## Usage

This plugin provides the following commands:

    $ lein jruby bundle
    $ lein jruby irb
    $ lein jruby rake
    $ lein jruby exec
    $ lein jruby -S XXXX
    $ lein jruby -e XXXX
    $ lein jruby -v

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

You can also see what version of JRuby is running:

    $ lein jruby -v
    jruby 1.6.7 (ruby-1.8.7-p357) (2012-02-22 3e82bc8) (Java HotSpot(TM) 64-Bit Server VM 1.6.0_29) [darwin-x86_64-java]

Eventually, an excplicit dependencies on a particular version of ruby in your project.clj will override this.  But for now it's pegged to 1.6.7

## TODO

+  add hooks

+  allow jruby in project.clj to override plugin's default version

