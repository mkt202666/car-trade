package com.pancosky.newcartrade.service;

import com.pancosky.newcartrade.dto.ChangePasswordDTO;
import com.pancosky.newcartrade.dto.LoginDTO;
import com.pancosky.newcartrade.dto.RegisterDTO;
import com.pancosky.newcartrade.vo.LoginVO;
import com.pancosky.newcartrade.vo.UserPublicVO;
import com.pancosky.newcartrade.vo.UserStatsVO;
import com.pancosky.newcartrade.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    LoginVO login(LoginDTO dto);
    UserVO register(RegisterDTO dto);
    UserVO getCurrentUser();
    UserVO updateProfile(UserVO vo);
    UserVO uploadAvatar(MultipartFile file);
    UserStatsVO getStats();
    void certify();
    UserPublicVO getUserPublicInfo(Long id);
    void changePassword(ChangePasswordDTO dto);
    void updatePhone(String newPhone, String smsCode);
    LoginVO refresh(String refreshToken);
}
