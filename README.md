---
This application generates check outputs in CSV format based on provided input arguments via the console.

### How to Use

1. **Compile All Files in Terminal:**

   ```bash
   javac -d ./out -sourcepath ./src ./src/main/java/ru/clevertec/check/*.java
   ```

   This command compiles all Java files in the `src` directory and outputs the compiled `.class` files to the `out` directory.


2. **Run Application with Required Arguments:**

   To run the application, use the following command structure:

   ```bash
   java -cp ./out main.java.ru.clevertec.check.CheckRunner id-quantity discountCard=xxxx balanceDebitCard=xxxx pathToFile=XXXX saveToFile=xxxx
   ```

   Replace `id-quantity`, `discountCard=xxxx`, `balanceDebitCard=xxxx`, `pathToFile=XXXX` and `saveToFile=xxxx` with your specific arguments. Here's an example:

   ```bash
   java -cp ./out main.java.ru.clevertec.check.CheckRunner 4-2 discountCard=1111 balanceDebitCard=110.01 pathToFile=./src/main/resources/products.csv saveToFile=./result.csv
   ```

3. **Output**

   All output data will be saved in the **result.csv** file.

### Error Handling

If an error occurs, you will receive one of the following error messages:

- **BAD REQUEST**: Indicates an input error, such as incorrect arguments.
- **NOT ENOUGH MONEY**: Indicates insufficient balance compared to the total cost.
- **INTERNAL SERVER ERROR**: Indicates any other unexpected error.

---