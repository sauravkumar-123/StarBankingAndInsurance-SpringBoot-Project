package com.starbanking.ServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.starbanking.DAO.UserRepository;
import com.starbanking.Enum.EnumsStatus.UserRole;
import com.starbanking.Enum.EnumsStatus.YesNO;
import com.starbanking.Exception.GlobalException;
import com.starbanking.Model.MainWallet;
import com.starbanking.Model.User;
import com.starbanking.Service.AdminService;
import com.starbanking.Utility.StringUtil;
import com.starbanking.Utility.Utility;

@Service
public class AdminServiceImpl implements AdminService {

	private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Override
	public User registerAdmin(User user) {
		User userData = userRepository.getUserDetails(user.getEmailid(), user.getMobileno(), 'Y');
		if (null == userData) {
			User userRegistration = new User();
			userRegistration.setApplicantName(user.getApplicantName());
			userRegistration.setEmailid(user.getEmailid());
			userRegistration.setMobileno(user.getMobileno());
			userRegistration.setDateOfBirth(user.getDateOfBirth());
			userRegistration.setBankingServiceStatus(YesNO.NO);
			userRegistration.setIsActive('Y');
			userRegistration.setRole(UserRole.ADMIN.getRoleName());
			userRegistration
					.setPassword(passwordEncoder.encode(StringUtil.generateDefaultPassword(user.getApplicantName())));
			userRegistration.setUserIdentificationNo(Utility.generateRandomfiveDigitNo());
			userRegistration.setUsername("AD" + StringUtil.getLastSixDigitOfMobileNo(user.getMobileno()));
			userRegistration.setParentUsername("Parent Details Not Avaliable!!");

			MainWallet mainWallet = new MainWallet();
			mainWallet.setUserName(userRegistration.getUsername());
			mainWallet.setCurrentBalance(0.00);
			mainWallet.setCommissionCredit(0.00);
			mainWallet.setCharges(0.00);
			mainWallet.setTds(0.00);
			mainWallet.setCreditAmount(0.00);
			mainWallet.setDebitAmount(0.00);
			mainWallet.setIsActive('Y');
			mainWallet.setCreditType(null);
			mainWallet.setDebitType(null);
			boolean Savestatus = userRepository.saveUserDetails(userRegistration, mainWallet);
			if (Savestatus) {
				logger.info("Admin and Mainwallet Details{}" + userRegistration + " {} " + mainWallet);
				return userRegistration;
			} else {
				return null;
			}
		} else {
			throw new GlobalException("Admin Details Already Avalibale");
		}
	}
}
