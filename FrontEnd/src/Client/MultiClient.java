/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import CommonUtils.CommonUtils;
import FrontEndIdl.FrontEnd;
import FrontEndIdl.FrontEndHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

/**
 *
 * @author s_ursimr
 */
public class MultiClient {
    
      public static void main(String[] args) {
        try {
      ORB orb = ORB.init(args, null);
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
           

 

 FrontEnd server= FrontEndHelper.narrow(ncRef.resolve_str(CommonUtils.FRONT_END));
        
         Runnable runnable1 = () ->
        {
        
                String response = server.bookEvent("MTLC1212", "MTLE031219", CommonUtils.CONFERENCE, "1");
                System.out.println(Thread.currentThread().getName() + ": " + " Response from server: " + response.replaceAll("NAT", "\n"));
            
          
        };

        Runnable runnable2 = () ->
        {
           
                String response = server.bookEvent("MTLC1212", "MTLM130722", CommonUtils.CONFERENCE, "1");
                System.out.println(Thread.currentThread().getName() + ": " + " Response from server: " + response.replaceAll("NAT", "\n"));
            
          
        };

        Runnable runnable3 = () ->
        {
         
                String response = server.swapEvent("MTLC1212", "MTLM130722", CommonUtils.CONFERENCE, "MTLE031219", CommonUtils.CONFERENCE);
                System.out.println(Thread.currentThread().getName() + ": " + " Response from server: " + response.replaceAll("NAT", "\n"));
            
         
        };

        Runnable runnable4 = () ->
        {
            
                String response = server.swapEvent("MTLC1212", "MTLM130720", CommonUtils.CONFERENCE, "MTLM130722", CommonUtils.CONFERENCE);
                System.out.println(Thread.currentThread().getName() + ": " + " Response from server: " + response.replaceAll("NAT", "\n"));
           
        };

        Runnable runnable5 = () ->
        {
            
                String response = server.cancelEvent("MTLC1212", "MTLE031219", CommonUtils.CONFERENCE);
                System.out.println(Thread.currentThread().getName() + ": " + " Response from server: " + response.replaceAll("NAT", "\n"));
            
           
        };

        Runnable runnable6 = () ->
        {
            
                String response = server.cancelEvent("MTLC1212", "MTLM130722", CommonUtils.CONFERENCE);
                System.out.println(Thread.currentThread().getName() + ": " + " Response from server: " + response.replaceAll("NAT", "\n"));
            
           
        };

        Thread thread1 = new Thread(runnable1);
        thread1.setName("Thread 1");

        Thread thread2 = new Thread(runnable2);
        thread2.setName("Thread 2");

        Thread thread3 = new Thread(runnable3);
        thread3.setName("Thread 3");

        Thread thread4 = new Thread(runnable4);
        thread4.setName("Thread 4");

        thread1.start();
        thread2.start();


        thread3.start();
        thread4.start();

//        Thread thread5 = new Thread(runnable5);
//        thread5.setName("Thread 5");
//
//        Thread thread6 = new Thread(runnable6);
//        thread6.setName("Thread 6");
//        
//        thread5.start();
//        thread6.start();
    }
        
          catch (Exception e) {
            System.out.println("Hello Client exception: " + e);
            e.printStackTrace();
        }
        
}
      
}
