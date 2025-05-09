/*
#########################################################
#                     IJA - project                     #
#         Authors: Urbánek Aleš, Kováčik Martin         #
#              Logins: xurbana00, xkovacm01             #
#                     Description:                      #
#                                                       #
#########################################################
*/

module ija.project.ijaproject {
    requires javafx.controls;
    requires javafx.fxml;


    opens ija.project.ijaproject to javafx.fxml;
    exports ija.project.ijaproject;
}