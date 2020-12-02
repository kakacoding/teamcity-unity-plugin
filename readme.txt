修改代码
UnityDetectorBase::findInstallations

添加环境变量
TEAMCITY_BUILD_INIT_PATH
d:\BuildAgent\lib\serviceMessages.jar;d:\BuildAgent\lib\runtime-util.jar;d:\BuildAgent\plugins\gradle-runner\lib\gradle-runner-agent.jar;d:\BuildAgent\plugins\gradle-runner\lib\gradle-runner-common.jar
JAVA_HOME
C:\Program Files\Java\jdk1.8.0_231

执行命令
D:\teamcity-unity-plugin-master>.\gradlew.bat --init-script D:\BuildAgent\plugins\gradle-runner\scripts\init.gradle -Dorg.gradle.daemon=false clean build 

替换.jar
d:\Buildagent\plugins\teamcity-unity-agent\lib\plugin-unity-agent.jar


ps:
plugin-unity-agent.jar是编译好的文件