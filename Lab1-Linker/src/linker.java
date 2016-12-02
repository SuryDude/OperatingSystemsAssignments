import java.io.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.Arrays;

class symbol{
	private String name;
	private int value = -111;
	private int mod_num;
	private String multiply_defined="";
	private boolean is_used = false;

	public symbol(String name, int value, int mod_num){
		this.name = name;
		this.value = value;
		this.mod_num = mod_num;

	}
	public symbol(String name, int mod_num){
		this.name = name;
		this.mod_num = mod_num;
	}
	public String getName(){
		return this.name;
	}
	public int getValue(){
		return this.value;
	}
	public int getModNum(){
		return this.mod_num;
	}
	public void isMultiplyDefined(){
		multiply_defined = "Error: This symbol is multiply defined; first value used.";
	}
	public void symbolUsed(){
		this.is_used = true;
	}
	public boolean isSymbolUsed(){
		return this.is_used;
	}
	@Override
	public String toString(){
		return this.name + " = " + this.value+" "+this.multiply_defined;
	}
}

class modulelist{
	private ArrayList<module> modules;
	private ArrayList<String> all_symbols_used = new ArrayList<>();
	private symboltable symbol_table = null;
	public modulelist(){
		modules = new ArrayList<>();
	}

	public void addModule(module m){
		modules.add(m);
	}
	public module findModule(int base_address){
		module m = null;
		for(int x=0; x<modules.size(); x++){
			if(modules.get(x).getBaseAddress() == base_address){
				m = modules.get(x);
			}
		}
		return m;
	}
	public void addSymbolToListUsed(String v){
		boolean isThere = false;
		for(int index=0; index<all_symbols_used.size(); index++){
			if(v.equals(all_symbols_used.get(index)))
				isThere  = true;
		}
		if(isThere == false){
			all_symbols_used.add(v);
		}
	}
	public void setSymbolTable(symboltable s){
		this.symbol_table = s;
	}
	public void checkIfDefinedButNotUsed(){
		boolean check = false;
		for(int y=0; y<this.symbol_table.sizeOfTable();y++){
			for(int x=0; x<all_symbols_used.size(); x++){
				if(this.symbol_table.getSymbol(y).getName().equals(all_symbols_used.get(x))){
					check = true;
				}
			}
			if(check == false){
				System.out.println("Warning: "+this.symbol_table.getSymbol(y).getName()+" was defined in module " +
						this.symbol_table.getSymbol(y).getModNum()+" but never used.");
			}
			check = false;
		}
	}
	public void checkIfAppearsNotUsedInModule(){
		for(int g=0; g<modules.size(); g++){
			modules.get(g).checkIfSymbolUsed();
		}
	}
	@Override
	public String toString(){
		String string = "Memory Map\n----------------------------------------------\n";
		for(int v=0; v<modules.size();v++){
			string = string + modules.get(v).toString();
		}
		return string;
	}
}

class module{
	private ArrayList<symbol> list_used = new ArrayList<>();
	private String[] words = {};
	private int base_address = 0;
	private String def_line = "";
	private int mod_num = 0;
	private int size_of_machine;

	public module(int base_address, String[] words, int mod_num, int size_of_machine){
		this.base_address = base_address;
		this.words = words;
		this.mod_num = mod_num;
		this.size_of_machine = size_of_machine;
	}
	public void setListUsed(ArrayList<symbol>list_used){
		this.list_used  = list_used;
	}
	public int getBaseAddress(){
		return this.base_address;
	}

	public void computeAddresses(){
		for(int i=0; i<words.length; i++){
			if(words[i].endsWith("1")){
				words[i] = words[i].substring(0,4);
			}
			else if(words[i].endsWith("2")){
				int value = Integer.parseInt(words[i].substring(1,4));
				if(value < size_of_machine)
					words[i] = words[i].substring(0,4);
				else
					words[i] = words[i].charAt(0)+"000 Error: Absolute address exceeds machine size; zero used.";
			}
			else if(words[i].endsWith("3")){
				int num = Integer.parseInt(words[i].substring(0,4));
				num = num+base_address;
				words[i] = num+"";
			}
			else if(words[i].endsWith("4")){
				int index = Integer.parseInt(words[i].substring(1,4));
				if(index < 0 || index >= list_used.size()){
					words[i] = words[i].substring(0,4) + " Error: External address exceeds length of use list; treated as immediate.";
				}
				else{
					symbol v = list_used.get(index);
					int wordAsInt = Integer.parseInt(words[i].substring(0,4))-index;
					if(v.getValue() == -111){
						v.symbolUsed();
						words[i] = wordAsInt+" Error: "+v.getName()+" is not defined; zero used.";
					}
					else{
						v.symbolUsed();
						wordAsInt = wordAsInt+v.getValue();
						words[i] = wordAsInt+"";
					}

				}
			}
			else{
				//do nothing, it must be an error
			}

		}
		this.words = words;
	}
	public void checkIfSymbolUsed(){
		for(int x=0; x<list_used.size(); x++){
			if(!list_used.get(x).isSymbolUsed()){
				System.out.println("Warning: In module "+this.mod_num+" "+list_used.get(x).getName()+
						" is on use list but isn't used.");
			}
		}
	}
	public void setDefLine(String l){
		String[] array = l.split(" ");
		String line = "";
		for(int i=0; i<array.length; i++){
			if(i%2 == 0)
				line = line + array[i]+" ";
		}
		this.def_line = line;

	}


	@Override
	public String toString(){
		String string = "";
		int tempBase = this.base_address;
		for(int c=0; c<words.length; c++){
			string = string+tempBase+": "+words[c]+"\n";
			tempBase++;
		}

		return string;
	}
}

class symboltable{
	private ArrayList<symbol> symbols;

	public symboltable(){
		symbols = new ArrayList<>();
	}
	public void addSymbol(symbol v){
		symbols.add(v);
	}
	public int sizeOfTable(){
		return symbols.size();
	}
	public symbol findSymbol(String name){
		symbol v = null;
		for(int j=0; j<symbols.size(); j++){
			if(symbols.get(j).getName().equals(name)){
				v = symbols.get(j);
			}
		}
		return v;
	}
	public int checkIfExists(String s_name){
		int check = -1;
		for(int h=0; h<symbols.size(); h++){
			if(symbols.get(h).getName().equals(s_name)){
				check = 1;
				symbols.get(h).isMultiplyDefined();
			}

		}
		return check;
	}
	public symbol getSymbol(int x){
		return symbols.get(x);
	}
	@Override
	public String toString(){
		String string = "Symbol Table\n----------------------------------------------\n";
		for(int x=0; x<symbols.size(); x++){
			string = string + symbols.get(x) + "\n";
		}
		return string;
	}
}

public class linker{
/*
	//store symbol and the module number it is in
	static HashMap<String, Integer> symbolDef = new HashMap<String, Integer>();
	//store symbol and its absolute address
	static HashMap<String, Integer> symbolTable = new HashMap<String, Integer>();
	//store module and its length
	static HashMap<Integer,Integer> moduleLen = new HashMap<Integer,Integer>();
	//store module number and its base location
	static HashMap<Integer,Integer>  moduleBase = new HashMap<Integer,Integer> ();
	//store which symbol(s) is used in this module (store symbol, start_loc)
	static HashMap<String, Integer> symbolInUse = new HashMap<String, Integer>();
	//store error message
	static ArrayList<String> errorMessage = new ArrayList<String>();
	*/
    /****************************FirstPass******************/
    /*public static void firstPass(String fileName) throws FileNotFoundException{
    	//relocationConstant = length of all the previous module
    	int relocationConstant = 0;
    	int moduleNumber = 1;
    	//read in the input file 
    	FileReader fileReader = new FileReader(fileName);
    	BufferedReader file = new BufferedReader(fileReader);
    	Scanner input = new Scanner(file);

    	//process the definition list
    	while (input.hasNextLine()){
    		moduleBase.put(moduleNumber, relocationConstant);
    		//find the symbol location
    		int number_of_defs = 0;

			if (input.hasNextInt()){
				number_of_defs = input.nextInt();
			}
    		for (int i = 0; i < number_of_defs; i++){
				int num_of_symbols = 0;
				if (input.hasNext()){
					num_of_symbols = input.nextInt();
				}

				String[] array_of_symbols = new String[num_of_symbols];
				int symbol_index = 0;
				String symbol;
				while (num_of_symbols > 0){
					if (!input.hasNextInt()){
						symbol = input.next();
						array_of_symbols[symbol_index] = symbol;
						symbol_index++;
						num_of_symbols--;
					}
					else{
						break;
					}


				}
				int relativeAddress = 0;
				if (input.findInLine("\n") != "\n" && !Arrays.asList(array_of_symbols).contains("z")){
					if (input.hasNextInt()) {
						relativeAddress = input.nextInt();
					}
				}

				for (String symbol_to_be_added : array_of_symbols) {
					//error handling goes here...
					if(symbolDef.get(symbol_to_be_added) != null){
						errorMessage.add("Error symbol " + symbol_to_be_added
								+ " is multiply defined, and the first definition is used");
					}
					else {
						symbolDef.put(symbol_to_be_added, moduleNumber);
						System.out.println("symbol added- " + symbol_to_be_added);
						symbolTable.put(symbol_to_be_added, relocationConstant+relativeAddress);
					}
				}

    		}
    		//skip the use list
    		int number_of_use = 0;
			if (input.hasNextInt()){
				number_of_use = input.nextInt();
			}
    		for (int j = 0; j < number_of_use; j++){
				if (input.hasNext()){
					input.next();
					if (input.hasNextInt()){
						input.nextInt();
					}
				}
    		}
    		//get the module length; and skip the rest of program text
    		int number_of_instructions = 0;
			if (input.hasNextInt()){
				number_of_instructions = input.nextInt();
			}
    		moduleLen.put(moduleNumber, number_of_instructions);
    		//update relocation constant for next module by adding the (previous) module length(number_of_instructions)
    		relocationConstant += number_of_instructions;
    		//skip the rest of program text content
    		for (int k = 0; k < number_of_instructions; k++){
				if (input.hasNext()){
					input.next();
					if (input.hasNextInt()){
						input.nextInt();
					}
				}
    		}
    		if (number_of_defs == 0 && number_of_instructions == 0 && number_of_use == 0) {
				break;
			}
    		moduleNumber++;
    	}
    	//handle if a symbol exceeds the size of module, last word in module is used
    	//handle if definition excees module size, last word is used
    	Set setOfKeys_1 = symbolDef.keySet();
		Iterator iterator_1 = setOfKeys_1.iterator();
		while(iterator_1.hasNext()){
			String key = (String) iterator_1.next();
			Integer value = (Integer) symbolDef.get(key);
			int baseAddress = (int) moduleBase.get(value);
			int absoluteAddress = (int) symbolTable.get(key);
			int moduleSize = (int) moduleLen.get(value);
			if (absoluteAddress - baseAddress >= moduleSize){
				errorMessage.add("Error symbol " + key
				+ " is defined at an address that exceeds the size of the module, and treat the relative address given as 0");
				symbolTable.put(key, baseAddress);
			}
		}

    	//symbol table creation 
        int[] valueList = new int[symbolTable.size()];
        Set setOfKeys_3 = symbolTable.keySet();
        Iterator iterator_3 = setOfKeys_3.iterator();
        int j = 0;
        while (iterator_3.hasNext()){
            String key = (String) iterator_3.next();
            Integer value = (Integer) symbolTable.get(key);
            valueList[j] = value;
            j++;
        }
        Arrays.sort(valueList);
    	System.out.println("Symbol Table\n");
        for (int i = 0; i < symbolTable.size(); i++){
    		int value = valueList[i];
            Set setOfKeys_2 = symbolTable.keySet();
            Iterator iterator_2 = setOfKeys_2.iterator();
            while (iterator_2.hasNext()){
                String key = (String) iterator_2.next();
                if ((int) symbolTable.get(key) == value){
                    System.out.format("%s = %d \n", key, value );
                    break;

                }
            }
    		

    	}   	

    }
*/
 	/****************************SecondPass***********************/
 	/*public static void secondPass(String fileName) throws FileNotFoundException {
 		//read in the input file 
    	FileReader fileReader = new FileReader(fileName);
    	BufferedReader file = new BufferedReader(fileReader);
    	Scanner input = new Scanner(file);
    	int index = 0; //keeps track of how many instructions within this module has we processed
    	int moduleNumber = 1;
    	int firstDigit = 0;
    	int correct_address = 0;   	
    	String symbol = new String();
    	System.out.println("\nMemory Map");

    	while(input.hasNext()){
    		HashMap<String, Integer> used_symbol_in_module = new HashMap<String, Integer>();
    		//helps to access the instruction starts at any relative loc in the module
    		HashMap<Integer, Integer> ins_location = new HashMap<Integer, Integer>();
    		    		//look up which type occurs at a particular location
    		HashMap<Integer, String> type_location = new HashMap<Integer, String>();
    		int relocationConstant = (int) moduleBase.get(moduleNumber);
    		//skip definition list
    		int number_of_defs = input.nextInt();
    		for (int i = 0; i < number_of_defs; i++){
    			input.next();
    			input.nextInt();
    		}
    		//process use list
    		int number_of_use = input.nextInt();
    		for (int i = 0; i < number_of_use; i++) {
    			symbol = input.next();
    			int start_loc = input.nextInt();
    			symbolInUse.put(symbol, start_loc); //symbol used across module
    			used_symbol_in_module.put(symbol, start_loc); //only store symbols used within the same module
    		}
    		//process program text
    		int number_of_instructions = input.nextInt();
    
    		for (int i = 0; i < number_of_instructions; i++) {
    			//a pair is (type, word)
    			String type = input.next();
    			int word = input.nextInt();    			
    			//store word in the ins_location hashmap
    			ins_location.put(i, word);
    			type_location.put(i, type);
    	
    		}
    		Set setOfKeys_2 = used_symbol_in_module.keySet();
			Iterator iterator_2 = setOfKeys_2.iterator();
			int counter = 0; //keeps track of how many times a use exceeds module size
			if (setOfKeys_2.isEmpty()){
				counter = number_of_instructions;
			}
			while(iterator_2.hasNext()){
				String key = (String) iterator_2.next();
				int moduleSize = (int) moduleLen.get(moduleNumber);
				int address_in_use = (int) symbolInUse.get(key);
				if (address_in_use > moduleSize){
					errorMessage.add("Error: symbol " + key
					+ " in use list has an address that exceeds the size of the module, the address has been treated as 777");
					ins_location.put(address_in_use, 1777);
					type_location.put(address_in_use,"E");
					counter = address_in_use + 1;

				}
				else {
					counter = number_of_instructions;
				}
			}   		
    		int[] corrected_address_list = new int[counter];   		
    		//warning: key was defined but the value was never used. goes here
    		//calculation 
    		//in case there is more than 1 symbol in a use list
    		Set setOfKeys = used_symbol_in_module.keySet();
    		Iterator iterator = setOfKeys.iterator();
    		if (setOfKeys.isEmpty()){
    			for (int i = 0; i < number_of_instructions; i++) {
    				if (corrected_address_list[i] == 0){
    					if (type_location.get(i).equals("A") || type_location.get(i).equals("I")) {
    						corrected_address_list[i] = (int)ins_location.get(i);
    					}
    					if (type_location.get(i).equals("R")){
    						corrected_address_list[i] = relocationConstant + (int)ins_location.get(i);

    					} 
    					if (type_location.get(i).equals("E")){
    						corrected_address_list[i] = (int)ins_location.get(i);
    						errorMessage.add("Error: at instruction" + i
    						+ "within module " + (moduleNumber-1)
    						+" E type address not on use chain; treated as I type.");
    					}
    				}
    			}
    			//display nicely!   			
    			for (int i = 0; i < number_of_instructions; i++) {
    				System.out.format("%d:	%d\n", index, corrected_address_list[i]);
    				index++;
    			}
    		}
    		while(iterator.hasNext()){
    			String symbol_in_use = (String) iterator.next();
    			Integer start_loc = (Integer)symbolInUse.get(symbol_in_use);
    			while (start_loc != 777){
                                    //error handle if symbol is used buy not defined.               
                    if(symbolDef.get(symbol_in_use) == null){
                        errorMessage.add("Error symbol " + symbol
                        + " is not defined, and its value is given zero at instruction "
                        + (start_loc+1) + " within module " + moduleNumber);
                        //start_loc = 0;
                        symbolTable.put(symbol, 0);
                    }
    				int temp_address = (int)ins_location.get(start_loc);
    				if (type_location.get(start_loc).equals("E")){
    					firstDigit = temp_address/1000;
    					Integer absoluteAddress = (Integer)symbolTable.get(symbol_in_use);
    					correct_address = firstDigit*1000+absoluteAddress;
    					corrected_address_list[start_loc] = correct_address;
    					start_loc = temp_address%1000;
    					//check if the next address directs you to an address that exceeds module size. 
    					if (start_loc != 777 && ((int)ins_location.get(start_loc))%1000 != 777 && ins_location.get(((int)ins_location.get(start_loc))%1000) == null){
    						errorMessage.add("Error: symbol " + symbol_in_use
    						+ " in use list has an address that exceeds the size of the module, the address has been treated as 777");
    						corrected_address_list[start_loc] = ((int)ins_location.get(start_loc)/1000)*1000+(int)symbolTable.get(symbol_in_use);
    						break;
    					}
    				}
    				else {
    					String misplaced_symbol = (String)type_location.get(start_loc);
    					errorMessage.add("Error: " + misplaced_symbol
    					+ " type address on use chain; treated as E type.");
    					firstDigit = temp_address/1000;
    					Integer absoluteAddress = (Integer)symbolTable.get(symbol_in_use);
    					corrected_address_list[start_loc] = firstDigit*1000+absoluteAddress;
    					start_loc = temp_address%1000;  				
    				}
    			}
    		}
    		if (!setOfKeys.isEmpty()){
    			for (int i = 0; i < number_of_instructions; i++) {
    				if (corrected_address_list[i] == 0){
    					if (type_location.get(i).equals("A") || type_location.get(i).equals("I")) {
    						corrected_address_list[i] = (int)ins_location.get(i);
    					}
    					if (type_location.get(i).equals("R")){
    						corrected_address_list[i] = relocationConstant + (int)ins_location.get(i);
    					} 
    					if (type_location.get(i).equals("E")){
    						corrected_address_list[i] = (int)ins_location.get(i);
    						errorMessage.add("Error: at instruction " + i
    						+ " within module " + (moduleNumber-1) + ", E type address not on use chain; treated as I type.");
    					}

    				}
    			}    		    			
    			//display nicely!  			
    			for (int i = 0; i < number_of_instructions; i++) {
    				System.out.format("%d:	%d\n", index, corrected_address_list[i]);
    				index++;
    			}
    		}    		
    		//the end of the process for the current module; move to the next one.
    		moduleNumber++;
    	}
    	//error handling: symbol defined but not used 
    	Set symbolKeys_1 = symbolDef.keySet();
		Iterator iterator_1 = symbolKeys_1.iterator();
		while (iterator_1.hasNext()) {
			String key = (String) iterator_1.next();
			Integer value = (Integer) symbolDef.get(key);
			if (!symbolInUse.containsKey(key)) {				
				System.out.println("Warning: " + key
				+ " was defined in module " + (value-1) +" but never used.");
			}
		}
	}
*/
    /****************************SecondPass***********************/
 	/*public static void displayErrorMessage(ArrayList<String> errorMessage){
 		for (int i = 0; i < errorMessage.size(); i++){
 			System.out.println(errorMessage.get(i));
 		}
 	}*/
    /****************************Main***********************/
	public static void main(String[] args) throws IOException {
		//String fileName = args[0];
		//firstPass(fileName);
		//secondPass(fileName);
		//displayErrorMessage(errorMessage);
		if(args.length !=1){
			System.err.println("Don't forget to include the absolute file path as the first and only command line argument");
			System.exit(0);
		}

		//Reading the file line by line and storing each line in an array
		FileInputStream f_input = null;
		BufferedReader br = null;
		try {
			f_input = new FileInputStream(args[0]);
			br  = new BufferedReader(new InputStreamReader(f_input));
		}catch (Exception e){
			System.err.println("No such file could be found. Are you sure you used the absolute path?");
			System.exit(0);
		}


		ArrayList<String> lines  = new ArrayList<String>(); //The second need the input, we just read off this array


		String line = null;//Where the line from the reader will be stored
		String[] array_s;//Used to store the line as an array, helps with parsing
		String potential_line = "";//This is where we store the lines that we make from a module ex:deflist, uselist, textlist

		int index = 0;//We use this to control when a new line of a module is starting, for example when a new def list or use list starts

		int num_of_modules = 0;//Here we store the first number from each file which represents the number of modules

		//These booleans are used to notify the program when to start making a def list, use list etc.
		boolean is_num_of_modules = true;
		Boolean def = false;
		Boolean used = false;
		Boolean words = false;


		int num_of_elements = 0;//Keeps track of the number of elements in each list

		String[] program_text = null;

		symboltable symbol_table = new symboltable();//Created symbol table

		modulelist modules = new modulelist();

		int base_address = 0; // Keeps track of base address of each module

		int size_of_machine = 600;//Given value of machine in class instructions

		int module_num = 0; //Keeps track of which module is at
		for(int w=0; w<2; w++){
			if(w==0){//1st pass
				//Reading each line from the file

				while((line = br.readLine())!=null){
					lines.add(line);
					array_s = line.split(" ");

					for(int j=0; j<array_s.length; j++){
						if(array_s[j].matches("[0-9]+") || array_s[j].matches("[a-zA-Z]+")||array_s[j].length()>1){

							if(is_num_of_modules){//Get num of modules
								num_of_modules = Integer.parseInt(array_s[j]);
								is_num_of_modules = false;
								def = true;
								index = 1;
							}
							else if(def){//Get definition line
								if(index == 1){
									num_of_elements = Integer.parseInt(array_s[j]);
									index = 0;
								}
								potential_line = potential_line + array_s[j]+" ";
								int length = potential_line.split(" ").length;
								if(length == num_of_elements*2+1){
									String[] s = potential_line.split(" ");
									String name="";
									boolean check_name = false;
									boolean check_value = false;
									int value=0;
									for(int q=1; q<s.length; q++){
										if(q%2 == 1){
											name = s[q];
											check_name = true;
										}
										if(q%2==0){
											value = Integer.parseInt(s[q])+base_address;
											check_value = true;
										}
										if(check_name && check_value){
											if(symbol_table.checkIfExists(name) == -1){
												symbol v = new symbol(name, value, module_num);
												symbol_table.addSymbol(v);
											}
											check_name = false;
											check_value = false;
										}
									}

									def = false;
									used = true;
									index = 1;
									potential_line = "";
								}
							}
							else if(used){//Get used list
								if(index == 1){
									num_of_elements = Integer.parseInt(array_s[j]);
									index = 0;
								}
								potential_line = potential_line + array_s[j]+" ";
								int length  = potential_line.split(" ").length;
								if(length == num_of_elements+1){
									used = false;
									words = true;
									index = 1;
									potential_line = "";
								}
							}
							else if(words){//Get program text
								if(index == 1){
									num_of_elements = Integer.parseInt(array_s[j]);
									index = 0;
								}
								potential_line = potential_line + array_s[j]+" ";
								int length = potential_line.split(" ").length;
								if(length==num_of_elements+1){
									program_text = potential_line.split(" ");
									int new_length = program_text.length-1;
									String[] new_program_text = new String[new_length];
									for(int d=0; d<new_length; d++){
										new_program_text[d] = program_text[d+1];
									}
									words = false;
									def = true;
									index = 1;
									potential_line = "";
									module m = new module(base_address, new_program_text, module_num, size_of_machine);
									modules.addModule(m);
									base_address = base_address + num_of_elements;
									program_text = null;
									module_num++;
								}
							}
							else{
							}
						}
						else{
						}
					}
				}
				is_num_of_modules = true;
				def = false;
				used = false;
				words = false;
				base_address = 0;
				num_of_elements = 0;
				module_num = 0;
			}
			if(w == 1){//Second pass
				//Reading each line from the file
				String symbols_defined = "";
				int place = 0;
				while(place<lines.size()){
					line = lines.get(place);
					array_s = line.split(" ");

					for(int j=0; j<array_s.length; j++){
						if(array_s[j].matches("[0-9]+") || array_s[j].matches("[a-zA-Z]+")||array_s[j].length()>1){

							if(is_num_of_modules){//Get num of modules
								num_of_modules = Integer.parseInt(array_s[j]);

								is_num_of_modules = false;
								def = true;
								index = 1;
							}
							else if(def){//Get definition line
								if(index == 1){
									num_of_elements = Integer.parseInt(array_s[j]);
									index = 0;
								}
								potential_line = potential_line + array_s[j]+" ";
								int length = potential_line.split(" ").length;
								if(length == num_of_elements*2+1){
									module g = modules.findModule(base_address);
									String[] array_of_potential_line = potential_line.split(" ");
									for(int t=1; t<array_of_potential_line.length; t++){
										symbols_defined = symbols_defined + array_of_potential_line[t]+" ";
									}
									symbols_defined = "";
									def = false;
									used = true;
									index = 1;
									potential_line = "";
								}
							}
							else if(used){//Get used list
								if(index == 1){
									num_of_elements = Integer.parseInt(array_s[j]);
									index = 0;
								}
								potential_line = potential_line + array_s[j]+" ";
								int length  = potential_line.split(" ").length;
								if(length == num_of_elements+1){
									module temp = modules.findModule(base_address);
									ArrayList<symbol> symbols_used = new ArrayList<>();
									String[] potential_string_array = potential_line.split(" ");
									for(int s=1; s<potential_string_array.length; s++){
										symbol v = symbol_table.findSymbol(potential_string_array[s]);
										modules.addSymbolToListUsed(potential_string_array[s]);
										if(v!=null)
											symbols_used.add(v);
										else{
											v = new symbol(potential_string_array[s],module_num);
											symbols_used.add(v);
										}
									}
									temp.setListUsed(symbols_used);
									temp.computeAddresses();
									used = false;
									words = true;
									index = 1;
									potential_line = "";
								}
							}
							else if(words){//Get program text
								if(index == 1){
									num_of_elements = Integer.parseInt(array_s[j]);
									index = 0;
								}
								potential_line = potential_line + array_s[j]+" ";
								int length = potential_line.split(" ").length;
								if(length==num_of_elements+1){
									words = false;
									def = true;
									index = 1;
									potential_line = "";
									base_address = base_address + num_of_elements;
									module_num++;
								}
							}
							else{
							}
						}
						else{
						}
					}
					place++;
				}
			}
		}
		modules.setSymbolTable(symbol_table);
		System.out.println(symbol_table.toString());
		System.out.println(modules);
		modules.checkIfDefinedButNotUsed();
	}
}
