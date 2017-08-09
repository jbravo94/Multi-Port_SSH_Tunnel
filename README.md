# Multi-Port_SSH_Tunnel
A small java application which enables multiple SSH portforwarding on a destination server as a cross-plattform gui.

Download the working build from Multi-Port_SSH_Tunnel/standalone/Multi-Port_SSH_Tunnel.zip and extract them.


IMPORTANT:

- Add this line in the file /etc/ssh/sshd_config on the ssh server:

  GatewayPorts yes


- In the folder where the Multi-Port_SSH_Tunnel.jar is launchend, the files credentials.json and portforwardlist.json have to   be present.



For more information look at the wiki page of this project (https://github.com/jbravo94/Multi-Port_SSH_Tunnel/wiki)

Main Source Code: https://github.com/jbravo94/Multi-Port_SSH_Tunnel/tree/master/app/src/app/App.java
