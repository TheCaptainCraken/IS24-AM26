package it.polimi.ingsw;


import it.polimi.ingsw.controller.client.Controller;

import java.util.Scanner;


/**
 * JavaFX App
 */
public class AppForTestingLocal{
    public static void main(String[] args) {
        Controller controller = null;
        Scanner myObj = new Scanner(System.in);

        String choice;
        controller = new Controller();
        System.out.println("Choice your type of connection: RMI or Socket");

      do{
          choice = myObj.nextLine();

          switch(choice){
              case "RMI":
              case "Socket":
                  controller.createInstanceOfConnection(choice);
                  break;
              default:
                  System.out.println("Invalid choice, please insert RMI or Socket");
          }
      }while(!choice.equals("RMI") && !choice.equals("Socket"));

       System.out.println("Choice your type of view: TUI or GUI");
       do{
           choice = myObj.nextLine();
           switch(choice){
               case "TUI":
               case "GUI":
                   try {
                       controller.setView(choice);
                   } catch (InterruptedException e) {
                       System.out.println("Error in setting the view");
                       System.exit(0);
                   }
                   break;
               default:
                   System.out.println("Invalid choice, please insert TUI or GUI");
           }
       }while(!choice.equals("TUI") && !choice.equals("GUI"));

    }
}

