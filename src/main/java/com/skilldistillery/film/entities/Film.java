package com.skilldistillery.film.entities;

public class Film {
	private int id;
	private String rating;
	private String specialFeatures;
	private int languageId;
	private Integer releaseYear;

	public Film(int filmId, String title, String desc, short releaseYear, int langId, int rentDur, double rate,
			int length, double repCost, String rating, String features) {
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getSpecialFeatures() {
		return specialFeatures;
	}

	public void setSpecialFeatures(String specialFeatures) {
		this.specialFeatures = specialFeatures;
	}

	@Override
	public String toString() {
		return "Film [id=" + id + ", rating=" + rating + ", specialFeatures=" + specialFeatures + ", languageId="
				+ languageId + ", releaseYear=" + releaseYear + "]";
	}
	
	
}
