// ############################################################################
// # ไฟล์     : TripTableModel.java
// # ส่วน     : UI – คลาสแม่ของตัวเชื่อมตาราง (abstract)
// # หน้าที่   : ส่วนที่ตารางทุกตัวใช้ร่วมกัน: อ้างอิง Trip, ชื่อคอลัมน์, ชนิดข้อมูลของคอลัมน์
// # จุดสำคัญ : Abstraction, Inheritance (ExpenseTableModel / BalanceTableModel extends คลาสนี้)
// ############################################################################
package tripsplit.ui;

import tripsplit.model.Trip;

import javax.swing.table.AbstractTableModel;

/**
 * ตัวเชื่อมข้อมูลใน Trip เข้ากับ JTable (Abstract Class)
 * คลาสลูกบอกแค่ชื่อคอลัมน์/ชนิดข้อมูล แล้วเขียน getRowCount() กับ getValueAt() เอง
 */
public abstract class TripTableModel extends AbstractTableModel {

    // # 1. ข้อมูลที่ตารางทุกตัวใช้: ทริป + หัวคอลัมน์ + ชนิดข้อมูล (ใช้เลือก Renderer)
    protected final Trip trip;
    private final String[] columns;
    private final Class<?>[] types;

    // # 2. Constructor — ดึงข้อมูลจาก Trip โดยตรง ไม่เก็บข้อมูลซ้ำ
    protected TripTableModel(Trip trip, String[] columns, Class<?>[] types) {
        if (columns.length != types.length) {
            throw new IllegalArgumentException("จำนวนหัวคอลัมน์และชนิดข้อมูลต้องเท่ากัน");
        }
        this.trip = trip;
        this.columns = columns;
        this.types = types;
    }

    // # 3. เมธอดที่ JTable เรียก: จำนวนคอลัมน์ / ชื่อคอลัมน์ / ชนิดข้อมูล (เหมือนกันทุกตาราง)
    @Override
    public final int getColumnCount() {
        return columns.length;
    }

    @Override
    public final String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public final Class<?> getColumnClass(int column) {
        return types[column];
    }
}
