<?php

/**
 * This is the model class for table "tip".
 *
 * The followings are the available columns in table 'tip':
 * @property integer $ID
 * @property string $NAZIV
 * @property string $EM
 * @property integer $PROTOKOL
 * @property string $DATA
 *
 * The followings are the available model relations:
 * @property Senzor[] $senzors
 */
class Tip extends CActiveRecord
{
	/**
	 * @return string the associated database table name
	 */
	public function tableName()
	{
		return 'tip';
	}

	/**
	 * @return array validation rules for model attributes.
	 */
	public function rules()
	{
		// NOTE: you should only define rules for those attributes that
		// will receive user inputs.
		return array(
			array('NAZIV', 'required'),
			array('PROTOKOL', 'numerical', 'integerOnly'=>true),
			array('NAZIV', 'length', 'max'=>200),
			array('EM', 'length', 'max'=>10),
			array('DATA', 'length', 'max'=>20),
			// The following rule is used by search().
			// @todo Please remove those attributes that should not be searched.
			array('ID, NAZIV, EM, PROTOKOL, DATA', 'safe', 'on'=>'search'),
		);
	}

	/**
	 * @return array relational rules.
	 */
	public function relations()
	{
		// NOTE: you may need to adjust the relation name and the related
		// class name for the relations automatically generated below.
		return array(
			'senzors' => array(self::HAS_MANY, 'Senzor', 'TIP_ID'),
		);
	}

	/**
	 * @return array customized attribute labels (name=>label)
	 */
	public function attributeLabels()
	{
		return array(
			'ID' => 'ID',
			'NAZIV' => 'Naziv',
			'EM' => 'Em',
			'PROTOKOL' => 'Protokol',
			'DATA' => 'Data',
		);
	}

	/**
	 * Retrieves a list of models based on the current search/filter conditions.
	 *
	 * Typical usecase:
	 * - Initialize the model fields with values from filter form.
	 * - Execute this method to get CActiveDataProvider instance which will filter
	 * models according to data in model fields.
	 * - Pass data provider to CGridView, CListView or any similar widget.
	 *
	 * @return CActiveDataProvider the data provider that can return the models
	 * based on the search/filter conditions.
	 */
	public function search()
	{
		// @todo Please modify the following code to remove attributes that should not be searched.

		$criteria=new CDbCriteria;

		$criteria->compare('ID',$this->ID);
		$criteria->compare('NAZIV',$this->NAZIV,true);
		$criteria->compare('EM',$this->EM,true);
		$criteria->compare('PROTOKOL',$this->PROTOKOL);
		$criteria->compare('DATA',$this->DATA,true);

		return new CActiveDataProvider($this, array(
			'criteria'=>$criteria,
		));
	}

	/**
	 * Returns the static model of the specified AR class.
	 * Please note that you should have this exact method in all your CActiveRecord descendants!
	 * @param string $className active record class name.
	 * @return Tip the static model class
	 */
	public static function model($className=__CLASS__)
	{
		return parent::model($className);
	}
}
