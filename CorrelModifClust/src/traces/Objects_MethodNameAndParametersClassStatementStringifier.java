/*
 *  Objects_MethodNameAndParametersClassStatementStringifier.java
 * 
 *  Copyright (C) 2012-2013 Sylvain Lamprier, Tewfik Ziaidi, Lom Messan Hillah and Nicolas Baskiotis
 * 
 *  This file is part of CARE.
 * 
 *   CARE is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   CARE is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with CARE.  If not, see <http://www.gnu.org/licenses/>.
 */

package traces;

import java.util.ArrayList;


/**
 * 
 * Statement stringifier which considers the name of the method, its types of parameters and the names and classes of the caller and the callee.  
 *
 *
 * @author Sylvain Lamprier
 *
 */
public class Objects_MethodNameAndParametersClassStatementStringifier extends
		StatementsStringifier {

	/**
	 * Stringifies a statement according to the caller name, the calle name and the class of the method parameters.
	 * 
	 * The resulting string is in the form : Class(caller)[Name(caller)]:Class(callee)[Name(callee)].method(parameter classes)
	 *   
	 * @param st
	 * @return a string representing the statement used for equivalence purposes
	 */
	@Override
	public String getText(Statement st) {
		ObjectInstance caller=st.getSender();
		ObjectInstance callee=st.getReceiver();
		Method meth=st.getMethod();
		String c="";
		if (st.isClosing()){c="closing_";} 
		
		ObjectClass callerclass=caller.getObjectClass();
		ObjectClass calleeclass=callee.getObjectClass();
		StringBuilder stb=new StringBuilder();
		stb.append(caller.toString()+":"+callee.toString()+".");
		
		String m=meth.getName();
		if (m.compareTo("")==0){return("");}
		stb.append(c+m+"(");
		ArrayList<ObjectInstance> vars=meth.getParameters();
		double nvars=vars.size();
		for(int i=0;i<nvars;i++){
			ObjectInstance var=vars.get(i);
			stb.append(var.getObjectClass().getName());
			if(i<(nvars-1)){
				stb.append(";");
			}
		}
		stb.append(")");
		
		return stb.toString();
	}
	
	@Override
	public Statement getStatement(String s){
		if(s.length()==0){
			return new Statement();
		}
		String[] st=s.split(":");
		String[] st2=st[0].split("\\[");
		ObjectClass callerclass=new ObjectClass(st2[0]);
		ObjectInstance caller=new ObjectInstance(st2[1].substring(0, st2[1].length()-1),callerclass);
		st=st[1].split("\\.");
		st2=st[0].split("\\[");
		ObjectClass calleeclass=new ObjectClass(st2[0]);
		ObjectInstance callee=new ObjectInstance(st2[1].substring(0, st2[1].length()-1),calleeclass);
		boolean closing=false;
		if(st[1].startsWith("closing_")){
			closing=true;
			st[1]=st[1].substring(8,st[1].length());
		}
		st2=st[1].split("\\(");
		st2[1]=st2[1].substring(0, st2[1].length()-1);
		st=st2[1].split(";");
		ArrayList<ObjectInstance> vars=new ArrayList<ObjectInstance>();
		int i=1;
		for(String ss:st){
			if(ss.length()==0){
				continue;
			}
			ObjectInstance p=new ObjectInstance(i+"",new ObjectClass(ss));
			vars.add(p);
			i++;
		}
		Method meth=new Method(st2[0],vars,null);
		Statement ret=new Statement(caller,meth,callee);
		if(closing){
			ret.setIsClosing();
		}
		return ret;
	}
	
	public static void main(String[] args){
		ObjectClass ihm=new ObjectClass("UserIHM");
		ObjectInstance ihm0 = new ObjectInstance("ihm0", ihm);
		ObjectClass account=new ObjectClass("Account");
		ObjectInstance account0 = new ObjectInstance("account0", account);
		ObjectClass bank=new ObjectClass("Bank");
		ObjectInstance bank0 = new ObjectInstance("bank0",bank);
		ObjectClass atm=new ObjectClass("ATM");
		ObjectInstance atm0 = new ObjectInstance("atm0", atm);
		ObjectClass cons=new ObjectClass("Consortium");
		ObjectInstance cons0 = new ObjectInstance("cons0", cons);
		ObjectClass accGen = new ObjectClass("AccountGenerator");
		ObjectInstance accGen0 = new ObjectInstance("accGen0", accGen);
		
		ArrayList<ObjectInstance> actors = new ArrayList<ObjectInstance>();
		actors.add(ihm0);
		actors.add(cons0);
		actors.add(atm0);
		actors.add(bank0);
		actors.add(account0);
		actors.add(accGen0);
		int i=0;
		
		
		/*
		 * InsertCard Bloc 
		 */	
		ArrayList<ObjectInstance> pa=new ArrayList<ObjectInstance>();
		pa.add(account0);
		Statement st1=new Statement("S"+i++,atm0,new Method("checkAccount",pa,bank),bank0);
		Statement st2=new Statement("S"+i++,atm0,new Method("checkCard",new ArrayList<ObjectInstance>(),atm),atm0);
		
		Statement st3=new Statement("S"+i++,atm0,new Method("requestPass",new ArrayList<ObjectInstance>(),ihm),ihm0);
		
		Statement st4=new Statement("S"+i++,atm0,new Method("ejectCard",new ArrayList<ObjectInstance>(),ihm),ihm0);
		Statement st5=new Statement("S1"+i++,atm0,new Method("requestTakeCard",new ArrayList<ObjectInstance>(),ihm),ihm0);
		Statement st6=new Statement("S1"+i++,atm0,new Method("quit",new ArrayList<ObjectInstance>(),ihm),ihm0);
		Statement st7=new Statement("S1"+i++,atm0,new Method("blabla",new ArrayList<ObjectInstance>(),ihm),ihm0);
		
		Objects_MethodNameAndParametersClassStatementStringifier o=new Objects_MethodNameAndParametersClassStatementStringifier();
		
		String s=o.getText(st1);
		System.out.println(s);
		System.out.println(o.getText(o.getStatement(s)));
		
	}
	
}
