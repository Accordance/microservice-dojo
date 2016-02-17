namespace :mesos do
  desc "Start Mesos Environment"
  task start: [ 'container:mesos_slave:start', 'container:mesos_marathon:start' ]
  desc "Stop Mesos Environment"
  task stop: [ 'container:mesos_marathon:stop', 'container:zookeeper:stop' ]
end
