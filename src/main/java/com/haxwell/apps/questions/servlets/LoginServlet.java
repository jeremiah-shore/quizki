package com.haxwell.apps.questions.servlets;

/**
 * Copyright 2013,2014 Johnathan E. James - haxwell.org - jj-ccs.com - quizki.com
 *
 * This file is part of Quizki.
 *
 * Quizki is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Quizki is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Quizki. If not, see http://www.gnu.org/licenses.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import com.haxwell.apps.questions.constants.Constants;
import com.haxwell.apps.questions.entities.User;
import com.haxwell.apps.questions.managers.UserManager;
import com.haxwell.apps.questions.utils.StringUtil;

/**
 * Servlet implementation class LoginServlet
 */
//@ WebServlet("/LoginServlet")
public class LoginServlet extends AbstractHttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(LoginServlet.class.getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.log(Level.FINE, "Entered LoginServlet::doPost()...");
		
		log.log(Level.FINER, request.getParameterNames().toString());
		
		String fwdPage = null;
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		List<String> errors = new ArrayList<String>();
		
		if (StringUtil.isNullOrEmpty(username) || StringUtil.isNullOrEmpty(password)) {
			fwdPage = "/login.jsp";
			
			errors.add("Either username or password was not valid.");
			
			request.setAttribute(Constants.VALIDATION_ERRORS, errors);
		}
		else {
			log.log(Level.FINER, "..creating the UsernamePasswordToken");
			UsernamePasswordToken token = new UsernamePasswordToken(username, password);
			
			log.log(Level.FINER, "..calling SecurityUtils.getSubject()");
			Subject currentUser = SecurityUtils.getSubject(); // Each time we need the Shiro subject, we get it this way..
			log.log(Level.FINER, "..got the Subject from Shiro. ('" + currentUser.toString() + "')");

			log.log(Level.FINER, "..getting Session from the request");
			HttpSession session = request.getSession();

			try {
				log.log(Level.FINER, "..about to get user object for [" + username + "]");
				User user = UserManager.getUser(username);
				log.log(Level.FINER, "..got user object for [" + username + "]");
				
				log.log(Level.FINER, String.valueOf(token.getPassword()) + " / " + token.getUsername());
				if (user != null)
					log.log(Level.FINER, user.getPassword() + " / " + user.getUsername());
				log.log(Level.FINER, "-------=---=---===---------=-");
				
				currentUser.login(token);
				
				session.setAttribute(Constants.CURRENT_USER_ENTITY, user);
				
				fwdPage = (String)session.getAttribute("originallyRequestedPage");				
			}
			catch (AuthenticationException ae) {
				log.log(Level.FINER, "..OH NO! An AuthenticationException!!");
				ae.printStackTrace();
				fwdPage = "/failedLogin.jsp";
			}
			
			if (StringUtil.isNullOrEmpty(fwdPage)) {
				log.log(Level.FINER, "..nothing set for originallyRequestedPage, so fwd to index.jsp");
				fwdPage = "/index.jsp";
			}
		}

		log.log(Level.FINER, "about to redirect to: " + fwdPage);
		log.log(Level.FINE, "about to leave LoginServlet::doPost()");
		redirectToJSP(request, response, fwdPage);
	}
}
