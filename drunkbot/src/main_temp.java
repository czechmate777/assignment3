mport java.util.Scanner;
import java.util.Random;

public class DrunkBot {

        public static void main(String[] args) {
                    Scanner scan = new Scanner(System.in);
                            Random rand = new Random();
                                    String old = "";
                                            String curr = "";
                                                    
                                                    for(int i = 0; i <= 30; i++){
                                                                    old = curr;
                                                                                curr = scan.nextLine();
                                                                                            if (justSaid(old, curr) == true)
                                                                                                                System.out.println("Didn't you just say that?");
                                                                                                        else
                                                                                                                            System.out.println(response(rand.nextInt(3)));
                                                                                                                }

                                                                                                                    }
                                                                                                                        
                                                                                                                        /*This function takes two strings as inputs and linearly compares the characters in each to determine if they are the same.
                                                                                                                        *    * If the strings are found to be similar ( > 80% shared characters), it will print "Didn't you just say that?" */
                                                                                                                            public static boolean justSaid(String a, String b){
                                                                                                                                        double samechars = 0;
                                                                                                                                                try{
                                                                                                                                                                
                                                                                                                                                                if (a.isEmpty() || b.isEmpty())
                                                                                                                                                                                    return false;
                                                                                                                                                                            
                                                                                                                                                                            if (a.length() > b.length()){
                                                                                                                                                                                            for (int i = 0; i < b.length(); i++){
                                                                                                                                                                                                                if (a.charAt(i) == b.charAt(i))
                                                                                                                                                                                                                                        samechars++;
                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                        if ((samechars / b.length() > 0.8))
                                                                                                                                                                                                                                                            return true;
                                                                                                                                                                                                                                                    else
                                                                                                                                                                                                                                                                        return false;
                                                                                                                                                                                                                                                                
                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                    else{
                                                                                                                                                                                                                                                                                    for (int i = 0; i < a.length(); i++){
                                                                                                                                                                                                                                                                                                        if (a.charAt(i) == b.charAt(i))
                                                                                                                                                                                                                                                                                                                                samechars++;
                                                                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                                                                                if ((samechars / b.length() > 0.8))
                                                                                                                                                                                                                                                                                                                                                    return true;
                                                                                                                                                                                                                                                                                                                                            else
                                                                                                                                                                                                                                                                                                                                                                return false;
                                                                                                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                                                                    
                                                                                                                                                                                                                                                                                                                                                                    catch (java.lang.ArithmeticException e){
                                                                                                                                                                                                                                                                                                                                                                                    return false;
                                                                                                                                                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                                                                                                                                                                    
                                                                                                                                                                                                                                                                                                                                                                                                    finally{
                                                                                                                                                                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                                                                                                                                                                                        
                                                                                                                                                                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                                                                                                                                                                        
                                                                                                                                                                                                                                                                                                                                                                                                                        /*If the input is not similar to the previous input, this method will be called and will return one of three randomly selected phrases.*/
                                                                                                                                                                                                                                                                                                                                                                                                                            public static String response(int n){
                                                                                                                                                                                                                                                                                                                                                                                                                                        String response = null;
                                                                                                                                                                                                                                                                                                                                                                                                                                                if (n == 0)
                                                                                                                                                                                                                                                                                                                                                                                                                                                                response = "How interesting";
                                                                                                                                                                                                                                                                                                                                                                                                                                                        if (n == 1)
                                                                                                                                                                                                                                                                                                                                                                                                                                                                        response = "Tell me more";
                                                                                                                                                                                                                                                                                                                                                                                                                                                                if (n == 2)
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                response = "Get fucked in the eye socket by a pineapple-dicked demon";
                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
                                                                                                                                                                                                                                                                                                                                                                                                                                                                        return response;
                                                                                                                                                                                                                                                                                                                                                                                                                                                                            }

}