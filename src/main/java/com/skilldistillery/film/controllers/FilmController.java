package com.skilldistillery.film.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.skilldistillery.film.data.DatabaseAccessor;
import com.skilldistillery.film.entities.Film;

@Controller
public class FilmController {

	@Autowired
	private DatabaseAccessor dao;
	
	@GetMapping(path={"", "/", "index", "index.do"        } )
	public ModelAndView home() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("index");
		return mv;
	}
	
	@GetMapping("find.do")
	public String findFilmById(@RequestParam("id") int id, 
			                   Model model) {
		Film film = dao.findFilmById(id);
		model.addAttribute("film", film);
		System.out.println(film);
		return "film";
	}
	
	public void anthonysMethod() {
		//Anthony wrote this code
		System.out.println("Ant");
	}

	public void sddeeMethod() {
		System.out.println("mine all mine!");
	}
}
