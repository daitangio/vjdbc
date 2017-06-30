@echo off
set JAVA_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=n

set VJDBC_CLASSPATH=.;hsqldb.jar;
for %%j in (..\lib\*.*) do call cpappend.bat %%j

java %JAVA_OPTS% -cp %VJDBC_CLASSPATH% de.simplicit.vjdbc.server.rmi.ConnectionServer vjdbc_config.xml
