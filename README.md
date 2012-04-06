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

## TODO

+  add hooks
