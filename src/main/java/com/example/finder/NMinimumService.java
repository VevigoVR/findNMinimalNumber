package com.example.finder;

import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Service
@NoArgsConstructor
public class NMinimumService {

    public int findNthMinimum(MultipartFile file, int n) throws Exception {
        if (n <= 0) {
            throw new IllegalArgumentException("N должно быть положительным числом");
        }

        int[] numbers = readNumbersFromExcel(file);

        if (n > numbers.length) {
            throw new IllegalArgumentException("N не может быть больше количества чисел в файле");
        }


        return findNthSmallestFixed(numbers, n); // 1
        // return findNthSmallestBasic(numbers, n); // 2
        // return findNthSmallest(numbers, n); // 3
    }

    private int[] readNumbersFromExcel(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows();
            int[] numbers = new int[rowCount];
            int validCount = 0;

            for (Row row : sheet) {
                Cell cell = row.getCell(0);
                if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                    numbers[validCount++] = (int) cell.getNumericCellValue();
                }
            }

            // Возвращаем массив только с валидными числами
            if (validCount < numbers.length) {
                int[] result = new int[validCount];
                System.arraycopy(numbers, 0, result, 0, validCount);
                return result;
            }

            return numbers;
        }
    }

    private int findNthSmallestFixed(int[] nums, int n) {
        if (n <= 0 || n > nums.length) {
            throw new IllegalArgumentException("Invalid n: " + n);
        }

        // Инициализируем массив максимальными значениями
        int[] minElements = new int[n];
        for (int i = 0; i < n; i++) {
            minElements[i] = Integer.MAX_VALUE;
        }

        for (int num : nums) {
            // Если текущее число меньше максимального в нашем массиве минимальных
            if (num < minElements[n - 1]) {
                // Находим позицию для вставки
                int pos = n - 1;

                // Сдвигаем элементы, чтобы освободить место
                while (pos > 0 && num < minElements[pos - 1]) {
                    minElements[pos] = minElements[pos - 1];
                    pos--;
                }

                // Вставляем число на найденную позицию
                minElements[pos] = num;
            }
        }

        return minElements[n - 1];
    }

    private int findNthSmallestBasic(int[] nums, int n) {
        int[] copy = nums.clone();
        int length = copy.length;

        // Частичная пузырьковая сортировка - только N проходов
        for (int i = 0; i < n; i++) {
            for (int j = length - 1; j > i; j--) {
                if (copy[j] < copy[j - 1]) {
                    // Меняем местами
                    int temp = copy[j];
                    copy[j] = copy[j - 1];
                    copy[j - 1] = temp;
                }
            }
        }
        return copy[n - 1]; // N-ный минимальный элемент
    }

    /**
     * Эффективный алгоритм поиска N-ного минимального числа
     * Используем максимальную кучу для хранения N минимальных элементов
     * Сложность: O(m log N), где m - количество чисел, N - параметр
     */
    private int findNthSmallest(int[] nums, int n) {
        // Максимальная куча (обратный порядок)
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(n, Collections.reverseOrder());

        for (int num : nums) {
            if (maxHeap.size() < n) {
                maxHeap.offer(num);
            } else if (num < maxHeap.peek()) {
                maxHeap.poll();
                maxHeap.offer(num);
            }
        }

        return maxHeap.peek();
    }
}