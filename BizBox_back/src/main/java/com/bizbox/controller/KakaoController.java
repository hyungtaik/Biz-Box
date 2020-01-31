package com.bizbox.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bizbox.Service.JwtService;
import com.bizbox.Service.UserServiceImpl;
import com.bizbox.apis.KakaoApi;
import com.bizbox.vo.User;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@CrossOrigin("*")
@RequestMapping("/kakao")
public class KakaoController {
	
	@Autowired
	private KakaoApi kakao;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private UserServiceImpl userService;
	
	@RequestMapping("/login")
	public ResponseEntity<Map<String, Object>> login(@RequestParam("refresh_token") String refresh_token, HttpSession session, HttpServletResponse res) {
		//https://kauth.kakao.com/oauth/authorize?client_id=64c7963937495c25ab3d30bc9f6e65e7&redirect_uri=http://70.12.246.137:8080/kakao/login&response_type=code
		System.out.println("refresh_token : " + refresh_token);
		String access_Token = kakao.getAccessToken(refresh_token);
        System.out.println("controller access_token : " + access_Token);
		HashMap<String, Object>userInfo = kakao.getUserInfo(access_Token);
		System.out.println(userInfo);
		String nickname = "";
		String email = "";
		if (userInfo.get("email") != null) {
			nickname = (String) userInfo.get("nickname");
			email = (String) userInfo.get("email");
	    }
		User newUser = new User(nickname, email);
		// jwt 변환
		Map<String, Object>resultMap = new HashMap<String, Object>();
		HttpStatus status = null;
		try {
			User temp = userService.checkUser(newUser);
			if(temp!=null) {	//기존회원이면
				System.out.println("기존회원");
				String token = jwtService.create(temp);
				res.setHeader("jwt-auth-token", token);
				resultMap.put("status", true);
				resultMap.put("data", temp);
			}else {
				System.out.println("가입하는중");
				userService.socialSingupUser(newUser);
				temp = userService.checkUser(newUser);
				String token = jwtService.create(temp);
				res.setHeader("jwt-auth-token", token);
				resultMap.put("status", true);
				resultMap.put("data", temp);
			}
			status = HttpStatus.ACCEPTED;
		}catch(Exception e) {
			log.error("카카오로그인 실패", e);
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
        return new ResponseEntity<Map<String,Object>>(resultMap, status);
	}
}