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

## Using RubyGems

The plugin is intened to work without a local installation of JRuby.  It uses the JRuby provided by the `org.jruby/jruby-complete`.  Thus, there won't be a natural RubyGems installation directory.  Instead, `lein-jruby` will install all your Gems to the `.lein-gems` directory under your project's root.

When you run:

    lein jruby gem install json

You'll see this:

    project-root/
    `-- .lein-gems/
        `-- rubygems/
            |-- bin/
            |-- cache/
            |-- doc/
            |-- gems/
                `-- json-1.6.6-java
            `-- specifications/
    

And if you install via Bundler it will be a little different (there will be a `jruby` dir instead of a `rubygems` dir).  But it will all be under `.lein-gems`.  Eventually this will be configurable. 

## TODO

+  add hooks

+  allow jruby in project.clj to override plugin's default version

