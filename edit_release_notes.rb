#!/usr/bin/env ruby

require 'optparse'

options = {}
OptionParser.new do |opts|
  opts.banner = "Usage: example.rb [options]"

  opts.on('-i', '--input INPUT', 'Input file name') { |v| options[:input_file] = v }
  opts.on('-o', '--output OUTPUT', 'Output file name') { |v| options[:output_file] = v }

end.parse!

input = options[:input_file]
if input.to_s.strip.length == 0
  # It's nil, empty, or just whitespace
  input = 'TEMP.md'
end

output = options[:output_file]
if output.to_s.strip.length == 0
  # It's nil, empty, or just whitespace
  output = 'release-notes.txt'
end

file = File.open(input, "rb")
contents = file.read
file.close

# remove links to github
clean_string = contents.gsub(/\(https[^)]*\)/, '')
# remove tool name
clean_string = clean_string.gsub(/\\\*[[:space:]]\*[^\*]*\*/,'')
# remove issue numbers
clean_string = clean_string.gsub(/\[\\\#\d+\]/,'')
clean_string = clean_string.gsub(/\[Full Changelog\]/,'')
clean_string = clean_string.gsub(/\# Change Log/, 'Release Notes')
# remove blank lines
clean_string = clean_string.gsub(/\r\n/,"\n")
clean_string = clean_string.squeeze("\n")
clean_string = clean_string.strip

File.open(output, 'w') {|f| f.write(clean_string) }