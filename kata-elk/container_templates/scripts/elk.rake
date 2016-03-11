namespace :elk do
  desc "Start ELK Environment"
  task start: [ 'container:elasticSearchData:start',
                'container:logData:start',
                'container:logstash:start',
                'container:elasticsearch:start',
                'container:kibana:start',
                'container:fileBeat:start'
                ]

  desc "Stop ELK Environment"
  task stop: [ 'container:kibana:stop',
               'container:logstash:stop',
               'container:elasticsearch:stop',
               'container:elasticSearchData:stop',
               'container:logData:stop',
               'container:fileBeat:stop']
end
