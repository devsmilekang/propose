import com.kms.propose.data.GetData;


public class Main {
	static public void main(String args[]){
		GetData a = new GetData();
		//http://121.254.179.10:8888/propose/getData.jsp?select=insertLoevMember&myPhoneNumber=01025003712&loveMemberPhone=01033214251&methodFlag=charged
		a.setSelect("buyItem");
		a.setMyPhoneNumber("01025003712");
		//a.setLoveMemberPhone("01033214251");
		a.setItemId("1");
		a.setMemberSession("636478256359857892785059452539167366508953049657210445422868");
		//System.out.println(a.dataSelect());
		//http://121.254.179.10:8888/propose/getData.jsp?select=buyItem&myPhoneNumber=01025003712&itemId=1&memberSession=636478256359857892785059452539167366508953049657210445422868
		a.dataSelect();
	}
}
