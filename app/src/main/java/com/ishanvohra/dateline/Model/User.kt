package com.ishanvohra.dateline.Model

data class User(val uid:String?= "",
                val name:String?="",
                val age:String?= "",
                val email:String?="",
                val gender:String?= "",
                val preferredGender:ArrayList<String>?=ArrayList<String>(),
                val imageUrl:String?="")


