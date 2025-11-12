package com.korit.korit_gov_servlet_study.ch02;

import lombok.NoArgsConstructor;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@WebServlet("/ch02/users")
public class UserServlet extends HttpServlet {
    private List<User> users;

    @Override
    public void init() throws ServletException {
        users = new ArrayList<>();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String name = req.getParameter("name");
        String email = req.getParameter("email");

        User user = User.builder()
                .username(username)
                .password(password)
                .name(name)
                .email(email)
                .build();

        Map<String, String> error = vaildUser(user);
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        //에러 응답
        if (!error.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(error);
            return;
        }

        vaildUser(user);

        users.add(user);
        System.out.println(users);
        //정상처리 응답
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setStatus(HttpServletResponse.SC_OK); //200
        resp.getWriter().println("사용자 등록 완료");

    }




    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //username으로 찾기
        //찾으면 user객체 응답(200), 없으면 username은 존재하지 않습니다.(404)
        String username = req.getParameter("username");
        if (username == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("username은 빈 공간일 수 없습니다");
            return;
        }

        Optional<User> foundUser = users.stream().filter(u -> u.getUsername().equals(username)).findFirst();
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        if (foundUser.isPresent()) {
            resp.setStatus(200);
            resp.getWriter().println(foundUser.get());
        } else {
            resp.setStatus(404);
            resp.getWriter().println(username+"을 찾을 수 없습니다.");
        }
1
//        Arrays.stream(username.getClass().getDeclaredFields()).forEach(u -> {
//            //private 필드에도 접근할 수 있게 강제로 접근 허용
//            u.setAccessible(true);
//            String fieldName = u.getName();
//            System.out.println(fieldName);
//
//        try {
//            String userName = u.getName();
//            System.out.println(userName);
//
//
//        }catch (RuntimeException e){
//
//        }
//
//        if (foundUser.isPresent()) {
//            resp.setStatus(HttpServletResponse.SC_OK);
//            try {
//                resp.getWriter().println(foundUser.get());
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }else {
//            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            try {
//                resp.getWriter().println("username이 존재하지 않습니다");
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//
//
//
//        }
//        });
    }

    private Map<String, String> vaildUser(User user){
        Map<String, String> error = new HashMap<>();
        //user 객체의 선언된 모든 필드(접근제이자 무관)를 스트림으로 순회
        Arrays.stream(user.getClass().getDeclaredFields()).forEach(f -> {
            //private 필드에도 접근할 수 있게 강제로 접근 허용
            f.setAccessible(true);
            String fieldName = f.getName();
            System.out.println(fieldName);

            try {
                //리플렉션으로 user 인스턴스의 해당 필드값 꺼내기
                Object fieldValue = f.get(user);
                System.out.println(fieldValue);
                //만약 해당 필드값이 null이면 검증 실패로 간주
                if (fieldValue == null){
                    throw new RuntimeException();
                }
                //필드값이 문자열일 때 공백/빈 문자열이 실패로 간주
                if (fieldValue.toString().isBlank()){
                    throw   new RuntimeException();
                }
            }catch (IllegalAccessException e) {
                //필드 접근 권한 문제(드물게 발생)
                System.out.println("필드에 접근할 수 없습니다");

            }catch (RuntimeException e){
                //위에서 던진 예외를 여기서 받아서 해당 필드에 대한 에러 메세지 추가
                error.put(fieldName, "빈 값일 수 없습니다");

            }
        });
        return error;

}



}
