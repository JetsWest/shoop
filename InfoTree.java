//Jackson Atkins
//CS143 
//HW8 - Hot or Cold
//2-24-2019
//This class creates a game of Hot or Cold that can be played by the user.
//It can create a game from a string or scanner input. Moreover, the game data
//can be saved in standard form if the user chooses to do so. The class can also
//insert nodes at certain positions in the tree. The class also has a inner static class
//that creates nodes used by the tree.
import java.io.*; 
import java.util.*;

public class InfoTree {

   private InfoLeaf overallRoot; //overallRoot of the binary tree
   
   //param: object - String that overallRoot will hold
   //post: creates new InfoTree of one node
   public InfoTree(String object) {
      overallRoot = new InfoLeaf(object);
   }

   //param: input - Scanner that will be used to create new InfoTree
   //post: creates new InfoTree using Scanner input
   public InfoTree(Scanner input) {
      overallRoot = buildTree(input);
   }

   //param: input - Scanner that will be used to create new InfoTree
   //post: creates new InfoTree using Scanner input
   private InfoLeaf buildTree(Scanner input) {
      InfoLeaf root = null;
      if (input.hasNext()) {
         if (input.nextLine().equals("G:")) {
            root = new InfoLeaf(input.nextLine());
            //Sets root to next in line if it is a guess node
         } else {
            root = new InfoBranch(input.nextLine(), buildTree(input), buildTree(input));
            //Builds to left and right if statement node
         }
      }
      return root;
   }

   //param: output - PrintStream where game data will be saved to
   //post: prints game in standard format to output
   public void saveGame(PrintStream output) {
      _saveGame(output, overallRoot);
   }
   
   //param: output - PrintStream where game data will be saved to.
   //root - InfoNode at top of tree holding game data
   //post: prints game in standard format to output
   private void _saveGame(PrintStream output, InfoLeaf root) {
      if (root != null) {    
         if (root.left == null && root.right == null) { //If it is a guess node
            output.println("G:");
            output.println(root.data);
         } else if (root.right == null) { //If only has a left branches
            output.println("S:");
            output.println(root.data);
            _saveGame(output, root.left);
         } else if (root.left == null) { //If only has a right branches
            output.println("S:");
            output.println(root.data);
            _saveGame(output, root.right);
         } else { //If has both left and right branches
            output.println("S:");
            output.println(root.data);
            _saveGame(output, root.left);
            _saveGame(output, root.right);
         }
      }
   }

   //param: user - UserInteraction object representing current user
   //post: plays full game of Hot or Cold
   public void playGame(UserInteraction user) {
      _playGame(user, overallRoot);
   }
   
   //param: user - UserInteraction object representing current user.
   //root - InfoNode representing current root that computer is guessing
   //post: plays game of Hot or Cold until correct, if not it can learn from mistakes
   private void _playGame(UserInteraction user, InfoLeaf root) {
      if (root.left == null && root.right == null && !root.data.toLowerCase().contains("it ")) {
         user.print("It is a " + root.data + ".");
      } else { //Prints out current root information
         user.print(root.data + ".");
      }
      if (user.nextBoolean()) {
         if (root.left == null && root.right == null) {
            user.println("I won! Thanks for the game."); //Case where computer won the game
         } else {
            _playGame(user, root.left); //Case where computer guessed right but game continues
         }
      } else {
         if (root.right == null && root.left == null) { //If they lose it can learn differences
            user.println("I lose. What is your item?");
            String answer = user.nextLine(); 
            user.println("Type a statement that distinguishes " + answer + " from a " + root.data);
            String question = user.nextLine(); 
            user.println("Is \"" + question + "\" Hot or Cold for " + answer + "?");
            String hotOrCold = user.nextLine(); 
            overallRoot = insertNode(overallRoot, root, answer, question, hotOrCold); 
            //Inserts new node into tree
         } else { 
            _playGame(user, root.right); //Plays game still if more possibilities
         }
      }
   }

   //param: main - node representing current search position. root - root that represents where 
   //split occurs. answer - string representing item. question - string representing differences
   //between answer and root data. hotOrCold - string from user whether it answer is hot or 
   //cold for question.
   //post: inserts node into correct position on tree
   private InfoLeaf insertNode(InfoLeaf main, InfoLeaf root, String answer,
                               String question, String hotOrCold) {
      if (overallRoot.left == null && overallRoot.right == null) { 
      //Case where tree only has one node
         if (hotOrCold.equalsIgnoreCase("hot")) {
            main = new InfoBranch(question, new InfoLeaf(answer), main);
         } else {
            main = new InfoBranch(question, main, new InfoLeaf(answer));
         }
      } else if (main.left != null && main.right != null) { 
         if (main.left.equals(root)) {
            if (hotOrCold.equalsIgnoreCase("hot")) {
               //Inserts statement node and above root node and places new possibility
               //on left as it is hot
               main.left = new InfoBranch(question, new InfoLeaf(answer), root);
            } else {
               //Inserts statement node and above root node and places new possibility
               //on right as it is cold
               main.left = new InfoBranch(question, root, new InfoLeaf(answer));
            }
         } else if (main.right.equals(root)) {
            if (hotOrCold.equalsIgnoreCase("hot")) {
               main.right = new InfoBranch(question, new InfoLeaf(answer), root);
            } else {
               main.right = new InfoBranch(question, root, new InfoLeaf(answer));
            }
         } else {
            //If neither match root then it searches both sides of tree
            main.left = insertNode(main.left, root, answer, question, hotOrCold);
            main.right = insertNode(main.right, root, answer, question, hotOrCold);
         }
      }
      return main;
   }

   //This class creates new InfoNodes that can be used by the InfoTree class.
   //It can create a node with no branches or it can create a node that has
   //branches with set data.
   private static class InfoLeaf {
      public final String data; //String representing data in node
      
      //param: data - string representing data in node.
      //left - left node from InfoNode. right - right node from InfoNode
      //post: creates new InfoNode with data and left and right
      public InfoLeaf(String data) {
          this.data = data;
      }
   }

   private static class InfoBranch extends InfoLeaf {
       public InfoBranch left; //Left choice of node on binary tree
       public InfoBranch right; //Right choice of node on binary tree

       public InfoBranch(String data, InfoBranch left, InfoBranch right) {
            super(data);
            this.left = left;
            this.right = right;
       }
   }
}