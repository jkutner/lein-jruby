
# require 'java'

# java_import "clojure.lang.RT"

# run lambda { |env|
#     [200, {'Content-Type' => 'text/html'}, "Hello, World: #{java.lang.System.getProperty("java.class.path")}"]
# }

require 'rubygems'
require 'bundler/setup'

#require 'bundler'
#Bundler.setup(:development)
#Bundler.setup(:foobar)

require 'sinatra'
require 'java'

spec = Gem::Specification.find_by_name("sinatra")
gem_root = spec.gem_dir
puts "I'm running from: #{gem_root}"
puts "My classpath is: #{java.lang.System.getProperty("java.class.path")}"

get '/' do
  #java_import "clojure.lang.RT"

  spec = Gem::Specification.find_by_name("sinatra")
  gem_root = spec.gem_dir
  gem_lib = gem_root + "/lib"

  "I'm running from: #{gem_root}"
end

run Sinatra::Application