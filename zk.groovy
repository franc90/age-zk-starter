#!/usr/bin/env groovy
import static Constants.getStopScript
import static Constants.getTargetDir

class Constants {
    static final sourceDir = 'zookeeper'
    static final zookeeperArchive = 'zookeeper-3.4.8.tar.gz'
    static final targetDir = '/tmp/zookeeper'
    static final confFile = 'conf/zoo.cfg'
    static final startScript = targetDir + '/bin/zkServer.sh'
    static final stopScript = targetDir + '/bin/zkServer.sh'
    static final cliScript = targetDir + '/bin/zkCli.sh'
}

def cleanup(args) {
    def cli = new CliBuilder(usage: 'cleanup.groovy -[cdhls]')

    cli.with {
        s longOpt: 'start', 'Start Zookeeper'
        c longOpt: 'close', 'Stop Zookeeper'
        l longOpt: 'logs', 'Delete all log files'
        d longOpt: 'delete', 'Delete all Zookeeper files'
        i longOpt: 'interactive', 'Launch ZookeeperCLI'
        h longOpt: 'help', 'Show help'
    }

    def options = cli.parse(args)

    if (!options || args.length == 0 || options.h) {
        cli.usage()
        return
    }

    if (options.c || options.l || options.d) {
        println 'Stop Zookeeper'
        stopZookeeper()
    }

    if (options.d) {
        println 'Delete all Zookeeper files'
        deleteZookeeperFiles()
    }

    if (options.l) {
        println 'Delete all log files'
        deleteLogFiles();
    }

    if (options.s) {
        println 'Start Zookeeper'
        startZookeeper()
    }

    if (options.i) {
        println 'Launch Zookeeper CLI'
        startZookeeperCLI()
    }
}

static def exists(String filePath) {
    File file = new File(filePath)
    return file.exists()
}

static def notExists(String filePath) {
    !exists(filePath)
}

def stopZookeeper() {
    if (notExists(stopScript)) {
        println "${stopScript} does not exist"
        return
    }

    Process p = "${stopScript} stop".execute()
    println p.text
    while (p.alive) {
        print "$p is still alive. Waiting another 200ms"
        sleep(200)
    }
}

def deleteZookeeperFiles() {
    if (notExists(targetDir)) {
        println "${targetDir} does not exist"
        return
    }

    new File(targetDir).deleteDir()
}

def deleteLogFiles() {
    println 'Not implemented'
}

def startZookeeper() {
    println 'Not implemented'
}

def startZookeeperCLI() {
    println 'Not implemented'
}

cleanup(args)