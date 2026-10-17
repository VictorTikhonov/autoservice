package ru.victortikhonov.autoserviceapp.model.Personnel;

public enum Role {

   ADMIN("Администратор"), MECHANIC("Механик"), OPERATOR("Оператор");

   private String value;

   Role(String value) {
      this.value = value;
   }

   public String getValue() {
      return value;
   }
}
