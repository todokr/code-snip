require 'sqlite3'

favicon_path = File.join(ENV['HOME'], '/Library/Application Support/Google/Chrome/Default/Favicons')
db = SQLite3::Database.new favicon_path

begin
  command = "delete from favicons where url like '%localhost%'"
  db.execute(command)
  p "Done: #{command}"
rescue SQLite3::BusyException => ex
  print <<-ERROR_DOC
  Chrome may be running. Quit and retry.
  ERROR_DOC
end
