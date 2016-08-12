# age-zk-starter

Zookeeper version of AgE multi agent distributed computation platform

## instalation 

There are provided two configuration templates - for cluster and standalone deployment


1. unpack zookeeper
2. zookeeper/zoo.cfg (zoo_cluster.cfg for cluster -rename to zoo.cfg)
  - clientPort - port that clients connect to
  - server.1=192.168.0.19:2888:3888
  - 1 - server node number
  - 192.168.0.19 - server IP
  - 2888 - quorum port
  - 3888 - leadership election port
  - create in dataDir file with server node id (current node) named myid
3. start ZK
4. update cluster.properties should contain list of zk ip:port - separated with commas
5. start app
