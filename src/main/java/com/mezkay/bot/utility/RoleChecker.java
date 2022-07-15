package com.mezkay.bot.utility;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;

public class RoleChecker {

    public static boolean playerIsAdmin(Member member) {
        boolean isAdmin = false;
        int i = 0;

        if(member.isOwner())
            isAdmin = true;

        while(i < member.getRoles().size() && !isAdmin) {
            String role = member.getRoles().get(i).getId();
            System.out.println(role);
            if(role.equals("799271597931364383") || role.equals("799005398328934422"))
                isAdmin = true;
            i++;
        }

        return isAdmin;
    }
}
