package la1;

public class EncryptVIC {

	/**
	 * Encrypt the given plain text password with VIC
	 * @param password Plain text password
	 * @param vicData Other VIC Encrypted Data(agentID, date, phrase, anagram)
	 * @return Encrypted password string
	 */
	public static String encrypt(String password, VICData vicData) {
		// set the message in VICData object to password
		vicData.message = password;

		// VIC encryption steps
		String step1 = VICOperations.noCarryAddition(vicData.agentID, vicData.date.substring(0, 5));
		String step2 = VICOperations.chainAddition(step1, 10);
		String step3 = VICOperations.digitPermutation(vicData.phrase);
		String step4 = VICOperations.noCarryAddition(step2, step3);
		String step5 = VICOperations.digitPermutation(step4);
		String step6and7 = VICOperations.checkerboardEncode(step5, vicData.anagram, vicData.message);

		return VICOperations.insertID(step6and7, vicData.agentID, vicData.date);
	}
}