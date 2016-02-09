namespace :env do
  desc 'Start Environment'
  task start: ['container:gateway-service:start', :create_profile]

  desc 'Populate Profiles-Service with Test data'
  task :create_profile do
    json = {'key': 'john', 'fullName': 'John Smith'}
    response = Utils.http_post("http://#{$environment[:dockerhost]}:8101/profiles", json)
    abort if response.code.to_i != 201
  end

  desc 'Stop Environment'
  task stop: ['container:demo-mysql:stop', 'container:demo-mongo:stop', 'container:config-service:stop']
end
