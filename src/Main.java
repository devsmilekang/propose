import com.kms.propose.data.GetData;


public class Main {
	static public void main(String args[]){
		GetData a = new GetData();
		//http://121.254.179.10:8888/propose/getData.jsp?select=heartCount&myPhoneNumber=01025003712
		a.setSelect("heartCount");
		a.setMyPhoneNumber("01025003712");
		a.dataSelect();
	}
}