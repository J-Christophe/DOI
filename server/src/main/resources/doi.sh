#! /bin/sh
case "$1" in
    start)
            echo -n "Starting DOI-server"
            java -Dlog4j.configurationFile=./log4j2.xml -jar ${project.build.finalName}.jar -f config.properties --start
            ;;
   stop) 
          echo -n "Stopping DOI-server"
          java -Dlog4j.configurationFile=./log4j2.xml -jar ${project.build.finalName}.jar -f config.properties --stop
           ;;
   restart)
          echo -n "Stopping DOI-server"
          java -Dlog4j.configurationFile=./log4j2.xml -jar ${project.build.finalName}.jar -f config.properties --stop
          echo -n "Starting SOI-server"
          java -Dlog4j.configurationFile=./log4j2.xml -jar ${project.build.finalName}.jar -f config.properties --start
          ;;
   version)
          echo -n "DOI-server version"
          java -Dlog4j.configurationFile=./log4j2.xml -jar ${project.build.finalName}.jar -f config.propertie --version
          ;;
        *)
          echo "Usage: doi.sh start|stop|restart"
          exit 1
          ;;   
    esac
